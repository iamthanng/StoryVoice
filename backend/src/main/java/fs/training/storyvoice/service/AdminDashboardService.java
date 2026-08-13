package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.response.AdminDashboardStatsResponse;
import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.enums.AccessLevel;
import fs.training.storyvoice.enums.UserRole;
import fs.training.storyvoice.repository.ChapterRepository;
import fs.training.storyvoice.repository.StoryRepository;
import fs.training.storyvoice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getDashboardStats() {
        long totalStories = storyRepository.count();
        long totalChapters = chapterRepository.count();
        long totalUsers = userRepository.count();
        long totalVipUsers = userRepository.findByRole(UserRole.ROLE_MEMBER, Pageable.unpaged())
                .getContent().stream().filter(u -> Boolean.TRUE.equals(u.getIsVip())).count();

        List<Chapter> allChapters = chapterRepository.findAll();
        long publicCount = allChapters.stream().filter(c -> c.getAccessLevel() == AccessLevel.PUBLIC).count();
        long memberCount = allChapters.stream().filter(c -> c.getAccessLevel() == AccessLevel.MEMBER).count();
        long vipCount = allChapters.stream().filter(c -> c.getAccessLevel() == AccessLevel.VIP).count();

        return AdminDashboardStatsResponse.builder()
                .totalStories(totalStories)
                .totalChapters(totalChapters)
                .totalUsers(totalUsers)
                .totalVipUsers(totalVipUsers)
                .publicChaptersCount(publicCount)
                .memberChaptersCount(memberCount)
                .vipChaptersCount(vipCount)
                .build();
    }
}
