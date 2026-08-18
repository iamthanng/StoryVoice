package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "FIELD_REQUIRED")
    private String token;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(min = 6, message = "FIELD_TOO_SHORT")
    private String newPassword;
}
