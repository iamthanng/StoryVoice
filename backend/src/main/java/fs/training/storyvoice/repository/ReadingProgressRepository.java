package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.ReadingProgress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {

    Optional<ReadingProgress> findByUserIdAndChapterId(Long userId, Long chapterId);

    List<ReadingProgress> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT DISTINCT s.genre.id FROM ReadingProgress rp JOIN rp.chapter c JOIN c.story s WHERE rp.user.id = :userId ORDER BY rp.updatedAt DESC")
    List<Long> findRecentGenreIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
