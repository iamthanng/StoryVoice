package fs.training.storyvoice.mapper;

import fs.training.storyvoice.dto.response.UserResponse;
import fs.training.storyvoice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * MapStruct tự động sinh class UserMapperImpl.java khi biên dịch.
 *
 * componentModel = "spring" → MapStruct sinh ra @Component,
 * cho phép @Autowired / @RequiredArgsConstructor inject UserMapper như một Spring Bean bình thường.
 *
 * Quy tắc mapping mặc định của MapStruct:
 *   - Trường cùng tên → tự map (vd: user.id → UserResponse.id)
 *   - Trường cùng tên khác kiểu → tự convert nếu có thể (vd: Enum → Enum)
 *   - Trường không tồn tại ở source → bỏ qua
 *
 * Với User entity:
 *   - id, username, email, role, isVip → đều có ở UserResponse → tự map hết, không cần @Mapping
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Chuyển đổi User entity → UserResponse DTO.
     * MapStruct sinh code tương đương:
     *
     * UserResponse toUserResponse(User user) {
     *     return UserResponse.builder()
     *         .id(user.getId())
     *         .username(user.getUsername())
     *         .email(user.getEmail())
     *         .role(user.getRole())
     *         .isVip(user.getIsVip())
     *         .build();
     * }
     */
    UserResponse toUserResponse(User user);
}
