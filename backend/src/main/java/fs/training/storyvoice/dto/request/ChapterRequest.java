package fs.training.storyvoice.dto.request;

import fs.training.storyvoice.enums.AccessLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChapterRequest {

    @NotNull(message = "FIELD_REQUIRED")
    private Long storyId;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 200, message = "FIELD_TOO_LONG")
    private String title;

    @NotBlank(message = "FIELD_REQUIRED")
    private String content;

    @NotNull(message = "FIELD_REQUIRED")
    private Integer chapterNumber;

    @NotNull(message = "FIELD_REQUIRED")
    private AccessLevel accessLevel;
}
