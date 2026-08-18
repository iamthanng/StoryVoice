package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.RatingComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingCommentRepository extends JpaRepository<RatingComment, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<RatingComment> findByStoryIdOrderByCreatedAtDesc(Long storyId, Pageable pageable);

    Optional<RatingComment> findByUserIdAndStoryId(Long userId, Long storyId);

    @Query("SELECT AVG(rc.rating) FROM RatingComment rc WHERE rc.story.id = :storyId")
    Double getAverageRatingByStoryId(@Param("storyId") Long storyId);
}
