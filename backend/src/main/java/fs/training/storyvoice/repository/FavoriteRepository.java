package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndStoryId(Long userId, Long storyId);

    Boolean existsByUserIdAndStoryId(Long userId, Long storyId);

    @EntityGraph(attributePaths = {"story", "story.author"})
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    void deleteByUserIdAndStoryId(Long userId, Long storyId);
}
