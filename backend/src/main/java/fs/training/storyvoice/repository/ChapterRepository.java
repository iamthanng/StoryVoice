package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    @EntityGraph(attributePaths = {"story"})
    List<Chapter> findByStoryIdOrderByChapterNumberAsc(Long storyId);

    @EntityGraph(attributePaths = {"story"})
    Optional<Chapter> findByStoryIdAndChapterNumber(Long storyId, Integer chapterNumber);

    // Lấy chương kế tiếp
    @EntityGraph(attributePaths = {"story"})
    Optional<Chapter> findFirstByStoryIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(Long storyId, Integer chapterNumber);

    // Lấy chương trước đó
    @EntityGraph(attributePaths = {"story"})
    Optional<Chapter> findFirstByStoryIdAndChapterNumberLessThanOrderByChapterNumberDesc(Long storyId, Integer chapterNumber);

    Long countByStoryId(Long storyId);

    @Override
    @EntityGraph(attributePaths = {"story"})
    Optional<Chapter> findById(Long id);
}
