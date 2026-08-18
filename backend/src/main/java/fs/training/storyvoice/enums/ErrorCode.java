package fs.training.storyvoice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // ---- Lỗi Authentication & Authorization ----
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để tiếp tục"),
    VIP_REQUIRED(HttpStatus.FORBIDDEN, "Tính năng này yêu cầu tài khoản VIP"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không chính xác"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    USERNAME_EXISTED(HttpStatus.BAD_REQUEST, "Tên đăng nhập đã được sử dụng"),
    EMAIL_EXISTED(HttpStatus.BAD_REQUEST, "Email đã được sử dụng"),

    // ---- Lỗi Đặt lại mật khẩu ----
    RESET_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Link đặt lại mật khẩu không hợp lệ"),
    RESET_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Link đặt lại mật khẩu đã hết hạn"),
    RESET_TOKEN_USED(HttpStatus.BAD_REQUEST, "Link đặt lại mật khẩu đã được sử dụng"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể gửi email. Vui lòng thử lại sau"),

    // ---- Lỗi Nghiệp vụ (Story/Chapter/Genre/Author) ----
    STORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy truyện"),
    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy chương"),
    AUTHOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy tác giả"),
    GENRE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy thể loại"),
    GENRE_EXISTED(HttpStatus.BAD_REQUEST, "Thể loại đã tồn tại"),
    AUDIO_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy file audio"),

    // ---- Lỗi File Storage & TTS ----
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "File không được để trống"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tải lên file"),
    TTS_TEXT_EMPTY(HttpStatus.BAD_REQUEST, "Nội dung văn bản để tạo giọng đọc không được để trống"),
    TTS_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi sinh giọng đọc từ API"),
    STREAM_AUDIO_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể phát audio"),
    AUDIO_NOT_EXISTS_ON_DISK(HttpStatus.NOT_FOUND, "Tập tin âm thanh không tồn tại trên ổ đĩa server"),

    // ---- Lỗi Generic ----
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống không xác định"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào không hợp lệ");

    private final HttpStatus httpStatus;
    private final String message;
}
