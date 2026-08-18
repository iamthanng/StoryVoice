package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.AudioFile;
import fs.training.storyvoice.enums.AudioSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

@Repository
public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {

    @EntityGraph(attributePaths = {"chapter"})
    Optional<AudioFile> findByChapterIdAndSource(Long chapterId, AudioSource source);

    @EntityGraph(attributePaths = {"chapter"})
    List<AudioFile> findByChapterId(Long chapterId);

    @EntityGraph(attributePaths = {"chapter"})
    Optional<AudioFile> findFirstByChapterIdOrderByCreatedAtDesc(Long chapterId);
}
