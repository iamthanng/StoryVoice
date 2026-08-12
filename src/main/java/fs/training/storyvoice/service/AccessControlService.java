package fs.training.storyvoice.service;

import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.enums.AccessLevel;
import fs.training.storyvoice.enums.UserRole;
import fs.training.storyvoice.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
public class AccessControlService {

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

        // 2. Nếu là Khách (chưa đăng nhập) -> Không xem được MEMBER hay VIP
        if (currentUser == null) {
            return false;
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
                throw new AccessDeniedException("Chương '" + chapter.getTitle() + "' yêu cầu đăng nhập để đọc/nghe");
            }
            if (chapter.getAccessLevel() == AccessLevel.VIP) {
                throw new AccessDeniedException("Chương '" + chapter.getTitle() + "' yêu cầu tài khoản VIP. Vui lòng nâng cấp VIP!");
            }
            throw new AccessDeniedException("Bạn không có quyền truy cập chương này");
        }
    }
}
