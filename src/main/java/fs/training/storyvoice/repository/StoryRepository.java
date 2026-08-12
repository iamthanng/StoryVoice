package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    Page<Story> findByGenreId(Long genreId, Pageable pageable);

    Page<Story> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT s FROM Story s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.author.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Story> searchStories(@Param("keyword") String keyword, Pageable pageable);

    Page<Story> findByGenreIdIn(List<Long> genreIds, Pageable pageable);
}
