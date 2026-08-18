package fs.training.storyvoice.service.impl;

import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.enums.AccessLevel;
import fs.training.storyvoice.enums.ErrorCode;
import fs.training.storyvoice.enums.UserRole;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.AccessControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service kiểm tra quyền truy cập chương tập trung (Core Access Control Mechanism).
 *
 * Mức khóa chương (AccessLevel):
 *   - PUBLIC : Mọi người dùng (kể cả Khách chưa đăng nhập).
 *   - MEMBER : Yêu cầu đã đăng nhập (ROLE_MEMBER hoặc ROLE_ADMIN).
 *   - VIP    : Yêu cầu tài khoản có isVip = true (hoặc ROLE_ADMIN).
 */
@Slf4j
@Service
public class AccessControlServiceImpl implements AccessControlService {

    /**
     * Kiểm tra xem người dùng hiện tại có quyền truy cập chương hay không.
     *
     * @param currentUser Thông tin người dùng hiện tại từ SecurityContext (có thể null nếu là Khách)
     * @param chapter Chương cần kiểm tra quyền
     * @return true nếu có quyền, false nếu bị chặn
     */
    public boolean canAccessChapter(UserPrincipal currentUser, Chapter chapter) {
        if (chapter == null) {
            return false;
        }

        AccessLevel level = chapter.getAccessLevel();

        // 1. Chương PUBLIC -> Ai cũng được xem
        if (level == AccessLevel.PUBLIC) {
            return true;
        }

        // 2. Nếu currentUser null → kiểm tra SecurityContext (admin gọi service với null vẫn được qua)
        if (currentUser == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdminContext = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(UserRole.ROLE_ADMIN.name()));
            if (isAdminContext) return true;
            return false;  // Khách thực sự — không có quyền truy cập MEMBER/VIP
        }

        // 3. Admin có toàn quyền truy cập mọi chương
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(UserRole.ROLE_ADMIN.name()));
        if (isAdmin) {
            return true;
        }

        // 4. Chương MEMBER -> Đã đăng nhập là xem được
        if (level == AccessLevel.MEMBER) {
            return true;
        }

        // 5. Chương VIP -> Cần cờ isVip = true
        if (level == AccessLevel.VIP) {
            return Boolean.TRUE.equals(currentUser.getIsVip());
        }

        return false;
    }

    /**
     * Kiểm tra quyền và ném AccessDeniedException (403) nếu không đủ quyền.
     */
    public void checkAccess(UserPrincipal currentUser, Chapter chapter) {
        if (!canAccessChapter(currentUser, chapter)) {
            if (currentUser == null) {
                throw new AppException(ErrorCode.LOGIN_REQUIRED);
            }
            if (chapter.getAccessLevel() == AccessLevel.VIP) {
                throw new AppException(ErrorCode.VIP_REQUIRED);
            }
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }
}
