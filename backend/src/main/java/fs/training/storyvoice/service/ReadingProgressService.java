package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.request.ReadingProgressRequest;
import fs.training.storyvoice.dto.response.ReadingProgressResponse;
import fs.training.storyvoice.security.UserPrincipal;

import java.util.List;

public interface ReadingProgressService {
    ReadingProgressResponse saveOrUpdateProgress(UserPrincipal currentUser, ReadingProgressRequest request);
    List<ReadingProgressResponse> getRecentProgress(UserPrincipal currentUser, int limit);
}
