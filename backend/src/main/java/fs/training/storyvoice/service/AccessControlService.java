package fs.training.storyvoice.service;

import fs.training.storyvoice.entity.Chapter;
import fs.training.storyvoice.security.UserPrincipal;

public interface AccessControlService {
    boolean canAccessChapter(UserPrincipal currentUser, Chapter chapter);
    void checkAccess(UserPrincipal currentUser, Chapter chapter);
}
