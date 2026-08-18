package fs.training.storyvoice.service.impl;

import fs.training.storyvoice.dto.response.StoryResponse;
import fs.training.storyvoice.entity.Story;
import fs.training.storyvoice.mapper.StoryMapper;
import fs.training.storyvoice.repository.ChapterRepository;
import fs.training.storyvoice.repository.ReadingProgressRepository;
import fs.training.storyvoice.repository.StoryRepository;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final ReadingProgressRepository readingProgressRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final StoryMapper storyMapper;

    /**
     * Thuật toán Gợi ý truyện tương tự:
     *   1. Nếu user đã đăng nhập: Lấy top 3 thể loại của các truyện mà user đọc/nghe gần nhất từ reading_progress.
     *   2. Truy vấn các truyện thuộc các thể loại đó.
     *   3. Nếu là Khách hoặc user chưa có lịch sử đọc: Trả về danh sách các truyện mới nhất.
     */
    @Transactional(readOnly = true)
    public List<StoryResponse> getRecommendedStories(UserPrincipal currentUser, int limit) {
        if (currentUser != null) {
            List<Long> recentGenreIds = readingProgressRepository.findRecentGenreIdsByUserId(
                    currentUser.getId(), PageRequest.of(0, 3));

            if (!recentGenreIds.isEmpty()) {
                Page<Story> recommended = storyRepository.findByGenreIdIn(
                        recentGenreIds, PageRequest.of(0, limit, Sort.by("createdAt").descending()));

                if (!recommended.isEmpty()) {
                    return mapStoriesToResponse(recommended.getContent());
                }
            }
        }

        // Fallback: Lấy danh sách truyện mới nhất
        Page<Story> latest = storyRepository.findAll(
                PageRequest.of(0, limit, Sort.by("createdAt").descending()));

        return mapStoriesToResponse(latest.getContent());
    }

    private List<StoryResponse> mapStoriesToResponse(List<Story> stories) {
        return stories.stream().map(story -> {
            StoryResponse response = storyMapper.toStoryResponse(story);
            response.setTotalChapters(chapterRepository.countByStoryId(story.getId()));
            return response;
        }).collect(Collectors.toList());
    }
}
