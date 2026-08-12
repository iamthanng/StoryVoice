package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.AuthorResponse;
import fs.training.storyvoice.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Client - Tác giả", description = "APIs xem danh sách tác giả cho độc giả")
public class PublicAuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Lấy tất cả tác giả")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(authorService.getAllAuthors()));
    }

    @Operation(summary = "Lấy chi tiết tác giả")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorService.getAuthorById(id)));
    }
}
