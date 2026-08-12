package fs.training.storyvoice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Generic API Response wrapper dùng chung cho tất cả endpoints.
 *
 * Ví dụ response thành công:
 * {
 *   "success": true,
 *   "message": "Đăng nhập thành công",
 *   "data": { ...AuthResponse... },
 *   "timestamp": "2026-08-10T10:00:00"
 * }
 *
 * Ví dụ response lỗi:
 * {
 *   "success": false,
 *   "message": "Username đã tồn tại",
 *   "data": null,
 *   "timestamp": "2026-08-10T10:00:00"
 * }
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Static factory methods tiện dụng
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Thành công", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
