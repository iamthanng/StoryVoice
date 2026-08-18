package fs.training.storyvoice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReadingProgressRequest {

    @NotNull(message = "FIELD_REQUIRED")
    private Long chapterId;

    /**
     * Vị trí lưu: giây audio thứ bao nhiêu hoặc vị trí cuộn trang. Mặc định 0.
     */
    private Integer lastPosition = 0;
}
