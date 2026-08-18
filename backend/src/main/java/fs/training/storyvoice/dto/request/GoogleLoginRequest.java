package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {

    /** Access Token từ Google (Authorization Code flow / Implicit flow) */
    @NotBlank(message = "FIELD_REQUIRED")
    private String accessToken;

    /** Email lấy từ Google userinfo (đã verify phía client) */
    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;

    /** Tên hiển thị từ Google */
    private String name;
}
