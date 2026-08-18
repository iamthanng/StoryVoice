package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.ForgotPasswordRequest;
import fs.training.storyvoice.dto.request.GoogleLoginRequest;
import fs.training.storyvoice.dto.request.LoginRequest;
import fs.training.storyvoice.dto.request.RegisterRequest;
import fs.training.storyvoice.dto.request.ResetPasswordRequest;
import fs.training.storyvoice.dto.response.AuthResponse;
import fs.training.storyvoice.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse loginWithGoogle(GoogleLoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
