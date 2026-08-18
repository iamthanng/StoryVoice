package fs.training.storyvoice.exception;

import fs.training.storyvoice.enums.ErrorCode;
import lombok.Getter;

/**
 * Exception tùy chỉnh dành riêng cho ứng dụng StoryVoice.
 * Hỗ trợ truyền ErrorCode để bắt lỗi và hỗ trợ đa ngôn ngữ ở frontend.
 */
@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
