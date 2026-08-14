package fs.training.storyvoice.exception;

import fs.training.storyvoice.enums.ErrorCode;
import lombok.Getter;

/**
 * Exception tùy chỉnh dành riêng cho ứng dụng StoryVoice.
 * Hỗ trợ truyền thêm errorCode để Frontend dễ dàng bắt lỗi.
 */
@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
