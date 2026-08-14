package fs.training.storyvoice.enums;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED),
    VIP_REQUIRED(HttpStatus.FORBIDDEN),
    ACCESS_DENIED(HttpStatus.FORBIDDEN);

    private final HttpStatus httpStatus;
}
