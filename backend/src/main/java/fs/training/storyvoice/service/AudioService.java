package fs.training.storyvoice.service;

import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.security.UserPrincipal;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;

public interface AudioService {
    ChapterResponse generateOrGetTtsAudio(Long chapterId, String voiceId, UserPrincipal currentUser);
    ResponseEntity<ResourceRegion> streamAudioResource(Long audioId, String rangeHeader);
}
