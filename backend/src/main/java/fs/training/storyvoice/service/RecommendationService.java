package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.response.StoryResponse;
import fs.training.storyvoice.security.UserPrincipal;

import java.util.List;

public interface RecommendationService {
    List<StoryResponse> getRecommendedStories(UserPrincipal currentUser, int limit);
}
