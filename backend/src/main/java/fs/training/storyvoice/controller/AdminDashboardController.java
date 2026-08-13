package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.AdminDashboardStatsResponse;
import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin - Thống kê Dashboard", description = "APIs thống kê số liệu tổng quan dành cho Admin Dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(summary = "Lấy số liệu thống kê tổng quan (Truyện, Chương, User, Tỷ lệ khóa chương)")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminDashboardService.getDashboardStats()));
    }
}
