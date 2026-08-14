package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.ForgotPasswordRequest;
import fs.training.storyvoice.dto.request.GoogleLoginRequest;
import fs.training.storyvoice.dto.request.LoginRequest;
import fs.training.storyvoice.dto.request.RegisterRequest;
import fs.training.storyvoice.dto.request.ResetPasswordRequest;
import fs.training.storyvoice.dto.response.AuthResponse;
import fs.training.storyvoice.dto.response.UserResponse;
import fs.training.storyvoice.entity.PasswordResetToken;
import fs.training.storyvoice.entity.User;
import fs.training.storyvoice.enums.UserRole;
import fs.training.storyvoice.mapper.UserMapper;
import fs.training.storyvoice.repository.PasswordResetTokenRepository;
import fs.training.storyvoice.repository.UserRepository;
import fs.training.storyvoice.security.JwtTokenProvider;
import fs.training.storyvoice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.google.client-id}")
    private String googleClientId;

    // ─── Đăng ký tài khoản ─────────────────────────────────────────────────────

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. Kiểm tra username và email đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' đã được sử dụng");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email '" + request.getEmail() + "' đã được sử dụng");
        }

        // 2. Tạo User mới với password đã mã hóa BCrypt
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_MEMBER)
                .isVip(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Đăng ký tài khoản mới thành công: {}", savedUser.getUsername());

        return userMapper.toUserResponse(savedUser);
    }

    // ─── Đăng nhập thường (Username + Password) ────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        // 1. AuthenticationManager sẽ gọi CustomUserDetailsService.loadUserByUsername
        // và so sánh password với BCrypt -> nếu sai sẽ ném BadCredentialsException
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        // 2. Tạo JWT Token từ đối tượng Authentication thành công
        String jwt = jwtTokenProvider.generateToken(authentication);

        // 3. Lấy thông tin User để đóng vào response
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        log.info("Đăng nhập thành công: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(jwt)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    /**
     * Luồng Google OAuth2:
     * Frontend dùng @react-oauth/google lấy access_token
     * gọi Google userInfo API lấy email/name rồi truyền xuống Backend.
     */
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        String email = request.getEmail();
        log.info("Google OAuth2 - Email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(email));

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());

        String jwt = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(jwt)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    // ─── Quên Mật Khẩu ─────────────────────────────────────────────────────────

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này."));

        // Xóa token cũ
        passwordResetTokenRepository.deleteAllByUserId(user.getId());

        // Tạo token mới
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Gửi email
        emailService.sendPasswordResetEmail(user.getEmail(), tokenString);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Link đặt lại mật khẩu không hợp lệ."));

        if (resetToken.getUsed()) {
            throw new RuntimeException("Link đặt lại mật khẩu đã được sử dụng.");
        }

        if (resetToken.isExpired()) {
            throw new RuntimeException("Link đặt lại mật khẩu đã hết hạn.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("User {} đã đặt lại mật khẩu thành công.", user.getEmail());
    }

    // ─── Private Helpers ────────────────────────────────────────────────────────

    private User createGoogleUser(String email) {
        // Tự tạo username từ email prefix (ví dụ: "nguyenvana@gmail.com" →
        // "nguyenvana")
        String baseUsername = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9_]", "");

        // Đảm bảo username không bị trùng
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        User newUser = User.builder()
                .username(username)
                .email(email)
                // Đặt random password vì user này sẽ chỉ đăng nhập bằng Google
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(UserRole.ROLE_MEMBER)
                .isVip(false)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Tự động tạo tài khoản mới từ Google OAuth2: username={}, email={}", savedUser.getUsername(),
                savedUser.getEmail());
        return savedUser;
    }
}
