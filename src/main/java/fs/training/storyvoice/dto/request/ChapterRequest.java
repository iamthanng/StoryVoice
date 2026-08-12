package fs.training.storyvoice.dto.request;

import fs.training.storyvoice.enums.AccessLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChapterRequest {

    @NotNull(message = "ID truyện không được để trống")
    private Long storyId;

    @NotBlank(message = "Tiêu đề chương không được để trống")
    @Size(max = 200, message = "Tiêu đề chương không được vượt quá 200 ký tự")
    private String title;

    @NotBlank(message = "Nội dung chương không được để trống")
    private String content;

    @NotNull(message = "Số thứ tự chương không được để trống")
    private Integer chapterNumber;

    @NotNull(message = "Mức độ truy cập (Công khai, Member, VIP) không được để trống")
    private AccessLevel accessLevel;
}
