package fs.training.storyvoice.dto.response;

import fs.training.storyvoice.enums.StoryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StoryResponse {
    private Long id;
    private String title;
    private Long authorId;
    private String authorName;
    private Long genreId;
    private String genreName;
    private String coverImage;
    private String description;
    private StoryStatus status;
    private Long totalChapters;
    private LocalDateTime createdAt;
}
