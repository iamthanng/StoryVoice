package fs.training.storyvoice.exception;

import fs.training.storyvoice.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.enums.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Bắt lỗi tập trung toàn bộ ứng dụng.
 * Mọi Exception ném ra từ bất kỳ Controller nào đều được xử lý ở đây.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Lỗi dữ liệu đầu vào không hợp lệ (validation @NotBlank, @Email, ...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.VALIDATION_FAILED.getMessage(), ErrorCode.VALIDATION_FAILED.name(), errors));
    }

    // Lỗi sai username/password khi đăng nhập
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(ErrorCode.INVALID_CREDENTIALS.getHttpStatus())
                .body(ApiResponse.error(ErrorCode.INVALID_CREDENTIALS.getMessage(), ErrorCode.INVALID_CREDENTIALS.name()));
    }

    // Lỗi không đủ quyền (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getHttpStatus())
                .body(ApiResponse.error(ErrorCode.ACCESS_DENIED.getMessage(), ErrorCode.ACCESS_DENIED.name()));
    }

    // Lỗi tùy chỉnh (AppException)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode != null ? errorCode.getHttpStatus() : HttpStatus.BAD_REQUEST;
        
        // Luôn trả về message từ Exception (hoặc fallback về ErrorCode) và tên của ErrorCode (ví dụ "USER_NOT_FOUND")
        return ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getMessage(), errorCode != null ? errorCode.name() : null));
    }

    // Lỗi nghiệp vụ từ Service (những chỗ chưa kịp refactor sang AppException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.UNCATEGORIZED_EXCEPTION.name()));
    }

    // Lỗi không xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus())
                .body(ApiResponse.error(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage(), ErrorCode.UNCATEGORIZED_EXCEPTION.name()));
    }
}
