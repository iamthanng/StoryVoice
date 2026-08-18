package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    @EntityGraph(attributePaths = { "author", "genre" })
    Page<Story> findByGenreId(Long genreId, Pageable pageable);

    @EntityGraph(attributePaths = { "author", "genre" })
    Page<Story> findByAuthorId(Long authorId, Pageable pageable);

    @EntityGraph(attributePaths = { "author", "genre" })
    @Query("SELECT s FROM Story s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.author.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Story> searchStories(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = { "author", "genre" })
    Page<Story> findByGenreIdIn(List<Long> genreIds, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "author", "genre" })
    Page<Story> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "author", "genre" })
    List<Story> findAll();

    @Override
    @EntityGraph(attributePaths = { "author", "genre" })
    Optional<Story> findById(Long id);
}
