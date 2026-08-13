package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findByStoryIdOrderByChapterNumberAsc(Long storyId);

    Optional<Chapter> findByStoryIdAndChapterNumber(Long storyId, Integer chapterNumber);

    // Lấy chương kế tiếp
    Optional<Chapter> findFirstByStoryIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(Long storyId, Integer chapterNumber);

    // Lấy chương trước đó
    Optional<Chapter> findFirstByStoryIdAndChapterNumberLessThanOrderByChapterNumberDesc(Long storyId, Integer chapterNumber);

    Long countByStoryId(Long storyId);
}
