package fs.training.storyvoice.repository;

import fs.training.storyvoice.entity.AudioFile;
import fs.training.storyvoice.enums.AudioSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {

    Optional<AudioFile> findByChapterIdAndSource(Long chapterId, AudioSource source);

    List<AudioFile> findByChapterId(Long chapterId);

    Optional<AudioFile> findFirstByChapterIdOrderByCreatedAtDesc(Long chapterId);
}
