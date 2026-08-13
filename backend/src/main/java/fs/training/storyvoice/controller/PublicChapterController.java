package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Client - Chương truyện", description = "APIs lấy danh sách chương và đọc nội dung chương truyện (có kiểm tra khóa chương)")
public class PublicChapterController {

    private final ChapterService chapterService;

    @Operation(summary = "Lấy danh sách chương của một truyện (Hiển thị trạng thái cờ isLocked dựa vào quyền người dùng)")
    @GetMapping("/stories/{storyId}/chapters")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getChaptersByStoryId(
            @PathVariable Long storyId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(chapterService.getChaptersByStoryId(storyId, currentUser)));
    }

    @Operation(summary = "Đọc nội dung chữ chương (Kiểm tra quyền truy cập: nếu không đủ quyền sẽ trả HTTP 403 Forbidden)")
    @GetMapping("/chapters/{id}")
    public ResponseEntity<ApiResponse<ChapterResponse>> getChapterContent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(chapterService.getChapterContent(id, currentUser)));
    }
}
