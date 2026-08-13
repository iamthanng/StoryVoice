package fs.training.storyvoice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReadingProgressResponse {
    private Long id;
    private Long storyId;
    private String storyTitle;
    private String coverImage;
    private Long chapterId;
    private Integer chapterNumber;
    private String chapterTitle;
    private Integer lastPosition;
    private LocalDateTime updatedAt;
}
