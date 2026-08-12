package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.request.AuthorRequest;
import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.AuthorResponse;
import fs.training.storyvoice.service.AuthorService;
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
@RequestMapping("/api/admin/authors")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin - Tác giả", description = "Quản lý Tác giả truyện cho Admin")
public class AdminAuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Lấy tất cả tác giả (Admin)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(authorService.getAllAuthors()));
    }

    @Operation(summary = "Tạo tác giả mới")
    @PostMapping
    public ResponseEntity<ApiResponse<AuthorResponse>> create(@Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo tác giả thành công", authorService.createAuthor(request)));
    }

    @Operation(summary = "Cập nhật tác giả")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> update(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật tác giả thành công", authorService.updateAuthor(id, request)));
    }

    @Operation(summary = "Xóa tác giả")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa tác giả thành công", null));
    }
}
