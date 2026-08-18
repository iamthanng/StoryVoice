package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.UserResponse;
import fs.training.storyvoice.entity.User;
import fs.training.storyvoice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin - User Management", description = "Quản lý người dùng dành cho Admin")
public class AdminUserController {

    private final UserRepository userRepository;

    @Operation(summary = "Lấy danh sách tất cả người dùng")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .isVip(user.getIsVip())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * PUT /api/admin/users/{id}/vip?grant=true  → Cấp VIP
     * PUT /api/admin/users/{id}/vip?grant=false → Thu hồi VIP
     */
    @Operation(summary = "Cấp hoặc thu hồi quyền VIP cho thành viên")
    @PutMapping("/{id}/vip")
    public ResponseEntity<ApiResponse<UserResponse>> toggleVip(
            @PathVariable Long id,
            @RequestParam boolean grant) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setIsVip(grant);
        userRepository.save(user);

        String message = grant
                ? "Đã cấp quyền VIP cho " + user.getUsername()
                : "Đã thu hồi quyền VIP của " + user.getUsername();

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isVip(user.getIsVip())
                .build();

        return ResponseEntity.ok(ApiResponse.success(message, response));
    }
}
