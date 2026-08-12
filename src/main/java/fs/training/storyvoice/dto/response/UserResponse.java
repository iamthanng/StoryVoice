package fs.training.storyvoice.dto.response;

import fs.training.storyvoice.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private Boolean isVip;
}
