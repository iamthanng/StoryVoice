package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.request.ChapterRequest;
import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.service.ChapterService;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/chapters")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin - Chương truyện", description = "Quản lý Chương truyện (Đặt accessLevel PUBLIC/MEMBER/VIP, Upload Audio thu sẵn)")
public class AdminChapterController {

    private final ChapterService chapterService;

    @Operation(summary = "Lấy danh sách chương của truyện cho Admin")
    @GetMapping("/story/{storyId}")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getChaptersByStory(@PathVariable Long storyId) {
        return ResponseEntity.ok(ApiResponse.success(chapterService.getChaptersByStoryId(storyId, null)));
    }

    @Operation(summary = "Lấy chi tiết một chương (bao gồm nội dung) cho Admin")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChapterResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(chapterService.getChapterContent(id, null)));
    }

    @Operation(summary = "Tạo chương mới (JSON)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChapterResponse>> create(
            @Valid @RequestBody ChapterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo chương truyện thành công", chapterService.createChapter(request)));
    }

    @Operation(summary = "Upload file audio thu sẵn cho chương")
    @PostMapping(value = "/{id}/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ChapterResponse>> uploadAudio(
            @PathVariable Long id,
            @RequestParam("audioFile") MultipartFile audioFile) {
        return ResponseEntity.ok(ApiResponse.success("Upload audio thành công", chapterService.uploadAudio(id, audioFile)));
    }

    @Operation(summary = "Cập nhật chương truyện (JSON)")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChapterResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ChapterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật chương truyện thành công", chapterService.updateChapter(id, request)));
    }

    @Operation(summary = "Xóa chương truyện")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa chương truyện thành công", null));
    }
}
