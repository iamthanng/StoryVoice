package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.request.ReadingProgressRequest;
import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.ReadingProgressResponse;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.ReadingProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Client - Tiến độ đọc/nghe", description = "APIs lưu vị trí tiến độ đọc chữ hoặc nghe audio và xem lịch sử gần đây")
public class ReadingProgressController {

    private final ReadingProgressService readingProgressService;

    @Operation(summary = "Lưu hoặc cập nhật tiến độ đọc/nghe (giây audio hoặc chương)")
    @PostMapping
    public ResponseEntity<ApiResponse<ReadingProgressResponse>> saveProgress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ReadingProgressRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Lưu tiến độ đọc thành công", 
                readingProgressService.saveOrUpdateProgress(currentUser, request)));
    }

    @Operation(summary = "Lấy danh sách lịch sử đọc/nghe gần đây của người dùng")
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<ReadingProgressResponse>>> getRecentProgress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(ApiResponse.success(readingProgressService.getRecentProgress(currentUser, limit)));
    }
}
