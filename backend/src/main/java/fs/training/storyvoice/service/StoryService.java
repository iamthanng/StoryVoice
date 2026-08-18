package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.StoryRequest;
import fs.training.storyvoice.dto.response.StoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface StoryService {
    Page<StoryResponse> getStories(Long genreId, Long authorId, String keyword, Pageable pageable);
    StoryResponse getStoryById(Long id);
    StoryResponse createStory(StoryRequest request);
    StoryResponse updateStory(Long id, StoryRequest request);
    StoryResponse uploadCoverImage(Long id, MultipartFile coverImage);
    void deleteStory(Long id);
}
