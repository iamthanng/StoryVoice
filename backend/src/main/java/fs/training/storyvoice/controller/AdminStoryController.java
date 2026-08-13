package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.request.StoryRequest;
import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.StoryResponse;
import fs.training.storyvoice.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/stories")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin - Truyện", description = "Quản lý Truyện cho Admin (Tạo truyện, Upload ảnh bìa)")
public class AdminStoryController {

    private final StoryService storyService;

    @Operation(summary = "Tạo truyện mới (JSON)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<StoryResponse>> create(
            @Valid @RequestBody StoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo truyện thành công", storyService.createStory(request)));
    }

    @Operation(summary = "Upload ảnh bìa truyện")
    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StoryResponse>> uploadCover(
            @PathVariable Long id,
            @RequestParam("coverImage") MultipartFile coverImage) {
        return ResponseEntity.ok(ApiResponse.success("Upload ảnh bìa thành công", storyService.uploadCoverImage(id, coverImage)));
    }

    @Operation(summary = "Cập nhật truyện (JSON)")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<StoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật truyện thành công", storyService.updateStory(id, request)));
    }

    @Operation(summary = "Xóa truyện")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        storyService.deleteStory(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa truyện thành công", null));
    }
}
