package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {

    /** Access Token từ Google (Authorization Code flow / Implicit flow) */
    @NotBlank(message = "Access Token không được để trống")
    private String accessToken;

    /** Email lấy từ Google userinfo (đã verify phía client) */
    @NotBlank
    @Email
    private String email;

    /** Tên hiển thị từ Google */
    private String name;
}
