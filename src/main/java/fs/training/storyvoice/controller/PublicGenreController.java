package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.GenreResponse;
import fs.training.storyvoice.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Tag(name = "Client - Thể loại", description = "APIs xem danh sách thể loại truyện cho độc giả")
public class PublicGenreController {

    private final GenreService genreService;

    @Operation(summary = "Lấy tất cả thể loại truyện")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(genreService.getAllGenres()));
    }

    @Operation(summary = "Lấy chi tiết thể loại")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(genreService.getGenreById(id)));
    }
}
