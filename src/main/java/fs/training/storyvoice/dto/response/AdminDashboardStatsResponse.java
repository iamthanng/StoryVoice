package fs.training.storyvoice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardStatsResponse {
    private Long totalStories;
    private Long totalChapters;
    private Long totalUsers;
    private Long totalVipUsers;
    private Long publicChaptersCount;
    private Long memberChaptersCount;
    private Long vipChaptersCount;
}
