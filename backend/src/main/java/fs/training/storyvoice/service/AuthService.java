package fs.training.storyvoice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import fs.training.storyvoice.dto.request.GoogleLoginRequest;
import fs.training.storyvoice.dto.request.LoginRequest;
import fs.training.storyvoice.dto.request.RegisterRequest;
import fs.training.storyvoice.dto.response.AuthResponse;
import fs.training.storyvoice.dto.response.UserResponse;
import fs.training.storyvoice.entity.User;
import fs.training.storyvoice.enums.UserRole;
import fs.training.storyvoice.mapper.UserMapper;
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

    // ─── Đăng nhập bằng Google (ID Token flow) ─────────────────────────────────

    /**
     * Luồng hoạt động đăng nhập Google:
     *
     * 1. React FE: User click "Đăng nhập Google" → dùng @react-oauth/google
     * 2. Google trả về { credential: "<ID_TOKEN>" } cho React
     * 3. React gửi POST /api/auth/google với body { idToken: "<ID_TOKEN>" }
     * 4. Backend dùng GoogleIdTokenVerifier để xác thực ID Token với Google
     * 5. Lấy email, name từ payload Google
     * 6. Tìm hoặc tạo User trong DB
     * 7. Tạo JWT Token → trả về cho React
     */
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        // 1. Verify ID Token với Google
        GoogleIdToken.Payload googlePayload = verifyGoogleIdToken(request.getIdToken());

        String email = googlePayload.getEmail();

        log.info("Google OAuth2 - Email: {}", email);

        // 2. Tìm user theo email hoặc tự động tạo mới nếu chưa có
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(email));

        // 3. Tạo Authentication object từ UserPrincipal
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());

        // 4. Tạo JWT Token
        String jwt = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(jwt)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    // ─── Private Helpers ────────────────────────────────────────────────────────

    private GoogleIdToken.Payload verifyGoogleIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Google ID Token không hợp lệ hoặc đã hết hạn");
            }

            return idToken.getPayload();
        } catch (Exception e) {
            log.error("Lỗi khi xác thực Google ID Token: {}", e.getMessage());
            throw new RuntimeException("Không thể xác thực với Google: " + e.getMessage());
        }
    }

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
