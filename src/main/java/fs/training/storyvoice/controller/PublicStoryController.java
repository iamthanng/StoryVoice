package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.StoryResponse;
import fs.training.storyvoice.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Tag(name = "Client - Truyện", description = "APIs xem danh sách truyện, tìm kiếm, phân trang và chi tiết truyện dành cho độc giả")
public class PublicStoryController {

    private final StoryService storyService;

    @Operation(summary = "Xem danh sách truyện (có phân trang, tìm kiếm, lọc thể loại / tác giả)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StoryResponse>>> getStories(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<StoryResponse> stories = storyService.getStories(genreId, authorId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(stories));
    }

    @Operation(summary = "Xem thông tin chi tiết một truyện")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoryResponse>> getStoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(storyService.getStoryById(id)));
    }
}
