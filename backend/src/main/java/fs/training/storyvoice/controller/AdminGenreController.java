package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.request.GenreRequest;
import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.GenreResponse;
import fs.training.storyvoice.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/genres")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin - Thể loại", description = "Quản lý Thể loại truyện cho Admin")
public class AdminGenreController {

    private final GenreService genreService;

    @Operation(summary = "Lấy tất cả thể loại (Admin)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(genreService.getAllGenres()));
    }

    @Operation(summary = "Tạo thể loại mới")
    @PostMapping
    public ResponseEntity<ApiResponse<GenreResponse>> create(@Valid @RequestBody GenreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thể loại thành công", genreService.createGenre(request)));
    }

    @Operation(summary = "Cập nhật thể loại")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> update(@PathVariable Long id, @Valid @RequestBody GenreRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thể loại thành công", genreService.updateGenre(id, request)));
    }

    @Operation(summary = "Xóa thể loại")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thể loại thành công", null));
    }
}
