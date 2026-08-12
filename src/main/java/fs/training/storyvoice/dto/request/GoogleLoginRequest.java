package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {

    /**
     * ID Token do React FE nhận từ Google (sau khi user đăng nhập Google thành công ở phía Client).
     * Backend sẽ dùng google-api-client để xác thực tính hợp lệ của ID Token này.
     *
     * Luồng ở Frontend (React):
     *   1. User bấm "Đăng nhập Google" → Dùng @react-oauth/google để login
     *   2. Google trả về { credential: "<ID_TOKEN>" }
     *   3. React gọi POST /api/auth/google với body: { idToken: "<ID_TOKEN>" }
     */
    @NotBlank(message = "Google ID Token không được để trống")
    private String idToken;
}
