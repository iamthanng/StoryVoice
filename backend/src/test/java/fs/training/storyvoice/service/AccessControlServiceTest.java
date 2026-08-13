package fs.training.storyvoice.service;

import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.enums.AccessLevel;
import fs.training.storyvoice.enums.UserRole;
import fs.training.storyvoice.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AccessControlServiceTest {

    private AccessControlService accessControlService;

    private Chapter publicChapter;
    private Chapter memberChapter;
    private Chapter vipChapter;

    private UserPrincipal memberUser;
    private UserPrincipal vipUser;
    private UserPrincipal adminUser;

    @BeforeEach
    void setUp() {
        accessControlService = new AccessControlService();

        publicChapter = Chapter.builder().id(1L).title("Chương 1 (Công khai)").accessLevel(AccessLevel.PUBLIC).build();
        memberChapter = Chapter.builder().id(2L).title("Chương 2 (Member)").accessLevel(AccessLevel.MEMBER).build();
        vipChapter = Chapter.builder().id(3L).title("Chương 3 (VIP)").accessLevel(AccessLevel.VIP).build();

        memberUser = new UserPrincipal(
                10L, "member1", "member1@gmail.com", "password", false,
                Collections.singletonList(new SimpleGrantedAuthority(UserRole.ROLE_MEMBER.name()))
        );

        vipUser = new UserPrincipal(
                11L, "vip1", "vip1@gmail.com", "password", true,
                Collections.singletonList(new SimpleGrantedAuthority(UserRole.ROLE_MEMBER.name()))
        );

        adminUser = new UserPrincipal(
                1L, "admin", "admin@gmail.com", "password", false,
                Collections.singletonList(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()))
        );
    }

    @Test
    @DisplayName("Khách chưa đăng nhập (currentUser = null) - Chỉ được xem chương PUBLIC")
    void testGuestAccess() {
        assertTrue(accessControlService.canAccessChapter(null, publicChapter));
        assertFalse(accessControlService.canAccessChapter(null, memberChapter));
        assertFalse(accessControlService.canAccessChapter(null, vipChapter));

        assertThrows(AccessDeniedException.class, () -> accessControlService.checkAccess(null, memberChapter));
        assertThrows(AccessDeniedException.class, () -> accessControlService.checkAccess(null, vipChapter));
    }

    @Test
    @DisplayName("Thành viên thường (isVip = false) - Được xem PUBLIC & MEMBER, bị chặn ở VIP")
    void testMemberAccess() {
        assertTrue(accessControlService.canAccessChapter(memberUser, publicChapter));
        assertTrue(accessControlService.canAccessChapter(memberUser, memberChapter));
        assertFalse(accessControlService.canAccessChapter(memberUser, vipChapter));

        assertDoesNotThrow(() -> accessControlService.checkAccess(memberUser, memberChapter));
        assertThrows(AccessDeniedException.class, () -> accessControlService.checkAccess(memberUser, vipChapter));
    }

    @Test
    @DisplayName("Thành viên VIP (isVip = true) - Được xem tất cả PUBLIC, MEMBER & VIP")
    void testVipAccess() {
        assertTrue(accessControlService.canAccessChapter(vipUser, publicChapter));
        assertTrue(accessControlService.canAccessChapter(vipUser, memberChapter));
        assertTrue(accessControlService.canAccessChapter(vipUser, vipChapter));

        assertDoesNotThrow(() -> accessControlService.checkAccess(vipUser, vipChapter));
    }

    @Test
    @DisplayName("Quản trị viên (ROLE_ADMIN) - Được xem tất cả các chương bất kể isVip")
    void testAdminAccess() {
        assertTrue(accessControlService.canAccessChapter(adminUser, publicChapter));
        assertTrue(accessControlService.canAccessChapter(adminUser, memberChapter));
        assertTrue(accessControlService.canAccessChapter(adminUser, vipChapter));

        assertDoesNotThrow(() -> accessControlService.checkAccess(adminUser, vipChapter));
    }
}
