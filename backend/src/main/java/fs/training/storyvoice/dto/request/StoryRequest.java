package fs.training.storyvoice.dto.request;

import fs.training.storyvoice.enums.StoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoryRequest {

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 200, message = "FIELD_TOO_LONG")
    private String title;

    @NotNull(message = "FIELD_REQUIRED")
    private Long authorId;

    @NotNull(message = "FIELD_REQUIRED")
    private Long genreId;

    private String description;

    private StoryStatus status;
}
