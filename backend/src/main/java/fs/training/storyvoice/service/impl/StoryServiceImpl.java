package fs.training.storyvoice.service.impl;

import fs.training.storyvoice.dto.request.StoryRequest;
import fs.training.storyvoice.dto.response.StoryResponse;
import fs.training.storyvoice.entity.Author;
import fs.training.storyvoice.entity.Genre;
import fs.training.storyvoice.entity.Story;
import fs.training.storyvoice.mapper.StoryMapper;
import fs.training.storyvoice.repository.AuthorRepository;
import fs.training.storyvoice.repository.ChapterRepository;
import fs.training.storyvoice.repository.GenreRepository;
import fs.training.storyvoice.repository.StoryRepository;
import fs.training.storyvoice.service.FileStorageService;
import fs.training.storyvoice.service.StoryService;
import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final ChapterRepository chapterRepository;
    private final StoryMapper storyMapper;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<StoryResponse> getStories(Long genreId, Long authorId, String keyword, Pageable pageable) {
        Page<Story> stories;
        if (StringUtils.hasText(keyword)) {
            stories = storyRepository.searchStories(keyword.trim(), pageable);
        } else if (genreId != null) {
            stories = storyRepository.findByGenreId(genreId, pageable);
        } else if (authorId != null) {
            stories = storyRepository.findByAuthorId(authorId, pageable);
        } else {
            stories = storyRepository.findAll(pageable);
        }

        return stories.map(story -> {
            StoryResponse response = storyMapper.toStoryResponse(story);
            response.setTotalChapters(chapterRepository.countByStoryId(story.getId()));
            return response;
        });
    }

    @Transactional(readOnly = true)
    public StoryResponse getStoryById(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        StoryResponse response = storyMapper.toStoryResponse(story);
        response.setTotalChapters(chapterRepository.countByStoryId(story.getId()));
        return response;
    }

    @Transactional
    public StoryResponse createStory(StoryRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));
        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));

        Story story = storyMapper.toStory(request);
        story.setAuthor(author);
        story.setGenre(genre);

        // File upload is handled via a separate API

        Story savedStory = storyRepository.save(story);
        StoryResponse response = storyMapper.toStoryResponse(savedStory);
        response.setTotalChapters(0L);
        return response;
    }

    @Transactional
    public StoryResponse updateStory(Long id, StoryRequest request) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));
        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));

        storyMapper.updateStoryFromRequest(request, story);
        story.setAuthor(author);
        story.setGenre(genre);

        // Cover image update is handled via a separate API

        Story updatedStory = storyRepository.save(story);
        StoryResponse response = storyMapper.toStoryResponse(updatedStory);
        response.setTotalChapters(chapterRepository.countByStoryId(updatedStory.getId()));
        return response;
    }

    @Transactional
    public StoryResponse uploadCoverImage(Long id, MultipartFile coverImage) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        if (coverImage != null && !coverImage.isEmpty()) {
            if (StringUtils.hasText(story.getCoverImage())) {
                fileStorageService.deleteFile(story.getCoverImage());
            }
            String newImagePath = fileStorageService.storeCoverImage(coverImage);
            story.setCoverImage(newImagePath);
            storyRepository.save(story);
        }
        
        StoryResponse response = storyMapper.toStoryResponse(story);
        response.setTotalChapters(chapterRepository.countByStoryId(story.getId()));
        return response;
    }

    @Transactional
    public void deleteStory(Long id) {
        Story story = storyRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        if (StringUtils.hasText(story.getCoverImage())) {
            fileStorageService.deleteFile(story.getCoverImage());
        }

        storyRepository.delete(story);
    }
}
