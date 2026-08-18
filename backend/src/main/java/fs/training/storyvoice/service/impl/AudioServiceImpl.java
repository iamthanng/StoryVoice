package fs.training.storyvoice.service.impl;

import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.entity.AudioFile;
import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.enums.AudioSource;
import fs.training.storyvoice.mapper.ChapterMapper;
import fs.training.storyvoice.repository.AudioFileRepository;
import fs.training.storyvoice.repository.ChapterRepository;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.enums.ErrorCode;
import fs.training.storyvoice.service.AccessControlService;
import fs.training.storyvoice.service.AudioService;
import fs.training.storyvoice.service.TextToSpeechService;
import fs.training.storyvoice.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioServiceImpl implements AudioService {

    private final ChapterRepository chapterRepository;
    private final AudioFileRepository audioFileRepository;
    private final AccessControlService accessControlService;
    private final TextToSpeechService textToSpeechService;
    private final FileStorageService fileStorageService;
    private final ChapterMapper chapterMapper;

    /**
     * Logic Cache & Sinh Audio TTS cho Chương:
     *   1. Kiểm tra quyền truy cập qua AccessControlService (Ném 403 nếu không đủ quyền)
     *   2. Kiểm tra CSDL xem chương đã có file audio TTS cache chưa
     *   3. Nếu ĐÃ CÓ: Trả về đường dẫn cache ngay (không tốn phí API)
     *   4. Nếu CHƯA CÓ: Gọi API TTS -> Lưu MP3 đĩa cứng -> Lưu DB -> Trả về DTO
     */
    @Transactional
    public ChapterResponse generateOrGetTtsAudio(Long chapterId, String voiceId, UserPrincipal currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        // 1. Kiểm tra quyền truy cập (Công khai, Member, VIP)
        accessControlService.checkAccess(currentUser, chapter);

        // 2. Kiểm tra Cache trong DB
        Optional<AudioFile> existingTts = audioFileRepository.findByChapterIdAndSource(chapterId, AudioSource.TTS);
        if (existingTts.isPresent()) {
            log.info("Dùng file TTS Audio đã cache trong CSDL cho chương ID: {}", chapterId);
            ChapterResponse response = chapterMapper.toChapterResponse(chapter);
            response.setIsLocked(false);
            response.setHasAudio(true);
            response.setAudioUrl(existingTts.get().getFilePath());
            response.setAudioSource(AudioSource.TTS);
            return response;
        }

        // 3. Nếu chưa có cache -> Gọi TTS API
        byte[] audioBytes = textToSpeechService.generateSpeech(chapter.getContent(), voiceId);

        // 4. Lưu file MP3 vào thư mục local /uploads/audio/tts/
        String filename = "chapter_" + chapterId + "_" + System.currentTimeMillis() + ".mp3";
        String storedPath = fileStorageService.storeTtsAudioBytes(audioBytes, filename);

        // 5. Lưu bản ghi AudioFile vào CSDL
        AudioFile audioFile = AudioFile.builder()
                .chapter(chapter)
                .filePath(storedPath)
                .source(AudioSource.TTS)
                .build();
        audioFileRepository.save(audioFile);

        ChapterResponse response = chapterMapper.toChapterResponse(chapter);
        response.setIsLocked(false);
        response.setHasAudio(true);
        response.setAudioUrl(storedPath);
        response.setAudioSource(AudioSource.TTS);

        return response;
    }

    /**
     * Hỗ trợ Audio Streaming với HTTP 206 Partial Content (Range Request)
     * Giúp trình duyệt phát âm thanh mượt mà và tua nhạc ở mọi giây.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ResourceRegion> streamAudioResource(Long audioId, String rangeHeader) {
        AudioFile audioFile = audioFileRepository.findById(audioId)
                .orElseThrow(() -> new AppException(ErrorCode.AUDIO_NOT_FOUND));

        // Cắt tiền tố "/uploads/" nếu có
        String cleanPath = audioFile.getFilePath().startsWith("/uploads/")
                ? audioFile.getFilePath().substring("/uploads/".length())
                : audioFile.getFilePath();

        Path uploadPath = fs.training.storyvoice.config.WebMvcConfig.resolveUploadPath("./uploads");
        Path filePath = uploadPath.resolve(cleanPath).toAbsolutePath().normalize();
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            throw new AppException(ErrorCode.AUDIO_NOT_EXISTS_ON_DISK);
        }

        try {
            long contentLength = resource.contentLength();
            HttpRange range = rangeHeader != null && rangeHeader.startsWith("bytes=")
                    ? HttpRange.parseRanges(rangeHeader).get(0)
                    : null;

            ResourceRegion region;
            if (range != null) {
                long start = range.getRangeStart(contentLength);
                long end = range.getRangeEnd(contentLength);
                long rangeLength = Math.min(1024 * 1024L, end - start + 1); // Đọc block 1MB
                region = new ResourceRegion(resource, start, rangeLength);
            } else {
                long rangeLength = Math.min(1024 * 1024L, contentLength);
                region = new ResourceRegion(resource, 0, rangeLength);
            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(region);

        } catch (Exception ex) {
            log.error("Lỗi khi stream file audio {}: {}", audioFile.getFilePath(), ex.getMessage());
            throw new AppException(ErrorCode.STREAM_AUDIO_FAILED);
        }
    }
}
