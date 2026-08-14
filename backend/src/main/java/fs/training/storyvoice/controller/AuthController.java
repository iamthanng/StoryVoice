package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.request.ForgotPasswordRequest;
import fs.training.storyvoice.dto.request.GoogleLoginRequest;
import fs.training.storyvoice.dto.request.LoginRequest;
import fs.training.storyvoice.dto.request.RegisterRequest;
import fs.training.storyvoice.dto.request.ResetPasswordRequest;
import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.AuthResponse;
import fs.training.storyvoice.dto.response.UserResponse;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Đăng ký, Đăng nhập, Đăng nhập Google")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Body: { "username": "...", "email": "...", "password": "..." }
     * Trả về: 201 Created kèm thông tin user mới tạo (không có password)
     */
    @Operation(summary = "Đăng ký tài khoản mới")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản thành công", userResponse));
    }

    /**
     * POST /api/auth/login
     * Body: { "username": "...", "password": "..." }
     * Trả về: 200 OK kèm { "accessToken": "eyJ...", "tokenType": "Bearer", "user": {...} }
     */
    @Operation(summary = "Đăng nhập bằng Username & Password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authResponse));
    }

    /**
     * POST /api/auth/google
     * Body: { "idToken": "<Google ID Token từ React>" }
     * Trả về: 200 OK kèm { "accessToken": "eyJ...", "user": {...} }
     *
     * Luồng ở React:
     *   1. Dùng @react-oauth/google: const { credential } = useGoogleLogin()
     *   2. Gọi: POST /api/auth/google với { idToken: credential }
     */
    @Operation(summary = "Đăng nhập bằng Google OAuth2 (Access Token flow)")
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request) {
        AuthResponse authResponse = authService.loginWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập Google thành công", authResponse));
    }

    @Operation(summary = "Yêu cầu đặt lại mật khẩu")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Nếu email tồn tại, link đặt lại mật khẩu đã được gửi.", null));
    }

    @Operation(summary = "Đặt lại mật khẩu mới")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Mật khẩu đã được đặt lại thành công.", null));
    }

    /**
     * GET /api/auth/me
     * Header: Authorization: Bearer <JWT>
     * Trả về thông tin người dùng hiện đang đăng nhập.
     */
    @Operation(summary = "Lấy thông tin người dùng hiện tại")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UserResponse response = UserResponse.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .isVip(currentUser.getIsVip())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
