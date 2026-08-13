package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorRequest {

    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự")
    private String name;

    private String bio;
}
