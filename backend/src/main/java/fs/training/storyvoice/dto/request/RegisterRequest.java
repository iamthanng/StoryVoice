package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(min = 3, max = 50, message = "FIELD_INVALID_LENGTH")
    private String username;

    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(min = 6, max = 100, message = "FIELD_INVALID_LENGTH")
    private String password;
}
