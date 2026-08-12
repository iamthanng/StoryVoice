package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.UserResponse;
import fs.training.storyvoice.entity.User;
import fs.training.storyvoice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin - User Management", description = "Quản lý người dùng dành cho Admin")
public class AdminUserController {

    private final UserRepository userRepository;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

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
