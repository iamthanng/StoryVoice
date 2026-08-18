package fs.training.storyvoice.service.impl;

import fs.training.storyvoice.dto.request.ReadingProgressRequest;
import fs.training.storyvoice.dto.response.ReadingProgressResponse;
import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.entity.ReadingProgress;
import fs.training.storyvoice.entity.User;
import fs.training.storyvoice.repository.ChapterRepository;
import fs.training.storyvoice.repository.ReadingProgressRepository;
import fs.training.storyvoice.repository.UserRepository;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.ReadingProgressService;
import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final ReadingProgressRepository readingProgressRepository;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReadingProgressResponse saveOrUpdateProgress(UserPrincipal currentUser, ReadingProgressRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        Optional<ReadingProgress> existingProgress = readingProgressRepository.findByUserIdAndChapterId(user.getId(), chapter.getId());

        ReadingProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setLastPosition(request.getLastPosition());
        } else {
            progress = ReadingProgress.builder()
                    .user(user)
                    .chapter(chapter)
                    .lastPosition(request.getLastPosition())
                    .build();
        }

        ReadingProgress saved = readingProgressRepository.save(progress);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReadingProgressResponse> getRecentProgress(UserPrincipal currentUser, int limit) {
        List<ReadingProgress> list = readingProgressRepository.findByUserIdOrderByUpdatedAtDesc(
                currentUser.getId(), PageRequest.of(0, limit));

        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ReadingProgressResponse toResponse(ReadingProgress rp) {
        Chapter c = rp.getChapter();
        return ReadingProgressResponse.builder()
                .id(rp.getId())
                .storyId(c.getStory().getId())
                .storyTitle(c.getStory().getTitle())
                .coverImage(c.getStory().getCoverImage())
                .chapterId(c.getId())
                .chapterNumber(c.getChapterNumber())
                .chapterTitle(c.getTitle())
                .lastPosition(rp.getLastPosition())
                .updatedAt(rp.getUpdatedAt())
                .build();
    }
}
