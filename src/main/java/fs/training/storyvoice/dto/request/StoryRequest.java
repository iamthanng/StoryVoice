package fs.training.storyvoice.dto.request;

import fs.training.storyvoice.enums.StoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoryRequest {

    @NotBlank(message = "Tiêu đề truyện không được để trống")
    @Size(max = 200, message = "Tiêu đề truyện không được vượt quá 200 ký tự")
    private String title;

    @NotNull(message = "ID tác giả không được để trống")
    private Long authorId;

    @NotNull(message = "ID thể loại không được để trống")
    private Long genreId;

    private String description;

    private StoryStatus status;
}
