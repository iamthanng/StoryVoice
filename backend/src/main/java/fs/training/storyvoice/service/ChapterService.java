package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.ChapterRequest;
import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.security.UserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChapterService {
    List<ChapterResponse> getChaptersByStoryId(Long storyId, UserPrincipal currentUser);
    ChapterResponse getChapterContent(Long chapterId, UserPrincipal currentUser);
    ChapterResponse createChapter(ChapterRequest request);
    ChapterResponse updateChapter(Long id, ChapterRequest request);
    ChapterResponse uploadAudio(Long id, MultipartFile audioFile);
    void deleteChapter(Long id);
}
