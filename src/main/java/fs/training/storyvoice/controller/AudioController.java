package fs.training.storyvoice.controller;

import fs.training.storyvoice.dto.response.ApiResponse;
import fs.training.storyvoice.dto.response.ChapterResponse;
import fs.training.storyvoice.security.UserPrincipal;
import fs.training.storyvoice.service.AudioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Client - Audio & TTS", description = "APIs nghe audio, gọi TTS AI chuyển văn bản thành giọng nói và Stream Audio HTTP 206")
public class AudioController {

    private final AudioService audioService;

    @Operation(summary = "Kích hoạt Nghe bằng AI (Gửi văn bản chương tới ElevenLabs TTS API hoặc lấy từ Cache)")
    @PostMapping("/chapters/{chapterId}/tts")
    public ResponseEntity<ApiResponse<ChapterResponse>> generateOrGetTts(
            @PathVariable Long chapterId,
            @RequestParam(required = false) String voiceId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ChapterResponse response = audioService.generateOrGetTtsAudio(chapterId, voiceId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Lấy file audio TTS thành công", response));
    }

    @Operation(summary = "Audio Streaming Endpoint (Hỗ trợ Header Range HTTP 206 Partial Content để tua audio)")
    @GetMapping("/audio/stream/{audioId}")
    public ResponseEntity<ResourceRegion> streamAudio(
            @PathVariable Long audioId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        return audioService.streamAudioResource(audioId, rangeHeader);
    }
}
