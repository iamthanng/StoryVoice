package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.ChapterRequest;
import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.entity.AudioFile;
import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.entity.Story;
import fs.training.storyvoice.enums.AudioSource;
import fs.training.storyvoice.mapper.ChapterMapper;
import fs.training.storyvoice.repository.AudioFileRepository;
import fs.training.storyvoice.repository.ChapterRepository;
import fs.training.storyvoice.repository.StoryRepository;
import fs.training.storyvoice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    private final AudioFileRepository audioFileRepository;
    private final ChapterMapper chapterMapper;
    private final AccessControlService accessControlService;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<ChapterResponse> getChaptersByStoryId(Long storyId, UserPrincipal currentUser) {
        if (!storyRepository.existsById(storyId)) {
            throw new RuntimeException("Không tìm thấy truyện với ID: " + storyId);
        }

        List<Chapter> chapters = chapterRepository.findByStoryIdOrderByChapterNumberAsc(storyId);

        return chapters.stream().map(chapter -> {
            ChapterResponse response = chapterMapper.toChapterResponse(chapter);
            
            // Tính toán cờ isLocked dựa vào AccessControlService
            boolean canAccess = accessControlService.canAccessChapter(currentUser, chapter);
            response.setIsLocked(!canAccess);

            // Kiểm tra thông tin audio
            Optional<AudioFile> audioOpt = audioFileRepository.findFirstByChapterIdOrderByCreatedAtDesc(chapter.getId());
            if (audioOpt.isPresent()) {
                response.setHasAudio(true);
                response.setAudioUrl(audioOpt.get().getFilePath());
                response.setAudioSource(audioOpt.get().getSource());
            } else {
                response.setHasAudio(false);
            }

            // Đối với danh sách tổng quan, ẩn nội dung chữ để tiết kiệm dung lượng JSON
            response.setContent(null);

            return response;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChapterResponse getChapterContent(Long chapterId, UserPrincipal currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương với ID: " + chapterId));

        // Kiểm tra quyền và ném exception 403 nếu không đủ quyền
        accessControlService.checkAccess(currentUser, chapter);

        ChapterResponse response = chapterMapper.toChapterResponse(chapter);
        response.setIsLocked(false);

        Optional<AudioFile> audioOpt = audioFileRepository.findFirstByChapterIdOrderByCreatedAtDesc(chapter.getId());
        if (audioOpt.isPresent()) {
            response.setHasAudio(true);
            response.setAudioUrl(audioOpt.get().getFilePath());
            response.setAudioSource(audioOpt.get().getSource());
        } else {
            response.setHasAudio(false);
        }

        return response;
    }

    @Transactional
    public ChapterResponse createChapter(ChapterRequest request) {
        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện với ID: " + request.getStoryId()));

        Chapter chapter = chapterMapper.toChapter(request);
        chapter.setStory(story);

        Chapter savedChapter = chapterRepository.save(chapter);

        // Upload file audio thu sẵn if needed via separate API

        return getChapterContent(savedChapter.getId(), null);
    }

    @Transactional
    public ChapterResponse updateChapter(Long id, ChapterRequest request) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương với ID: " + id));

        chapterMapper.updateChapterFromRequest(request, chapter);
        Chapter updatedChapter = chapterRepository.save(chapter);

        // Audio file update is handled via a separate API

        return getChapterContent(updatedChapter.getId(), null);
    }

    @Transactional
    public ChapterResponse uploadAudio(Long id, MultipartFile audioFile) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương với ID: " + id));

        if (audioFile != null && !audioFile.isEmpty()) {
            String audioPath = fileStorageService.storeAudioFile(audioFile);
            AudioFile audio = AudioFile.builder()
                    .chapter(chapter)
                    .filePath(audioPath)
                    .source(AudioSource.UPLOAD)
                    .build();
            audioFileRepository.save(audio);
        }

        return getChapterContent(chapter.getId(), null);
    }

    @Transactional
    public void deleteChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương với ID: " + id));

        // Xóa các file audio của chương này
        List<AudioFile> audioFiles = audioFileRepository.findByChapterId(id);
        for (AudioFile audio : audioFiles) {
            fileStorageService.deleteFile(audio.getFilePath());
        }

        chapterRepository.delete(chapter);
    }
}
