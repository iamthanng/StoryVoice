package fs.training.storyvoice.dto.response;

import fs.training.storyvoice.enums.AccessLevel;
import fs.training.storyvoice.enums.AudioSource;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChapterResponse {
    private Long id;
    private Long storyId;
    private String storyTitle;
    private String title;
    private String content; // Có thể null nếu bị khóa (403) hoặc trong danh sách rút gọn
    private Integer chapterNumber;
    private AccessLevel accessLevel;
    private Boolean isLocked;
    private Boolean hasAudio;
    private String audioUrl;
    private AudioSource audioSource;
    private LocalDateTime createdAt;
}
