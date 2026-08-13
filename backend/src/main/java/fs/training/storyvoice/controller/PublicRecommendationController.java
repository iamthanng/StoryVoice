package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.StoryResponse;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Tag(name = "Client - Gợi ý truyện", description = "API gợi ý truyện tương tự dựa trên lịch sử thể loại đọc/nghe gần nhất")
public class PublicRecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "Gợi ý truyện tương tự dựa trên thể loại người dùng đã đọc/nghe")
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<List<StoryResponse>>> getRecommended(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "6") int limit) {

        return ResponseEntity.ok(ApiResponse.success(recommendationService.getRecommendedStories(currentUser, limit)));
    }
}
