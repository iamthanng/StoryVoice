package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreRequest {

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 100, message = "FIELD_TOO_LONG")
    private String name;

    private String description;
}
