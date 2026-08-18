package fs.training.storyvoice.service.impl;

import fs.training.storyvoice.service.TextToSpeechService;
import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service tích hợp ElevenLabs Text-to-Speech API.
 * Chuyển đổi văn bản nội dung chương truyện thành luồng binary file âm thanh MP3.
 */
@Slf4j
@Service
public class ElevenLabsTextToSpeechServiceImpl implements TextToSpeechService {

    @Value("${app.tts.elevenlabs.api-key}")
    private String apiKey;

    @Value("${app.tts.elevenlabs.voice-id:21m00Tcm4TlvDq8ikWAM}")
    private String defaultVoiceId;

    @Value("${app.tts.elevenlabs.model-id:eleven_multilingual_v2}")
    private String modelId;

    private final RestTemplate restTemplate;

    public ElevenLabsTextToSpeechServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Gọi ElevenLabs API chuyển văn bản thành file âm thanh MP3 (byte[])
     *
     * @param text Nội dung chương truyện
     * @param customVoiceId Voice ID tùy chọn (nếu null sẽ dùng voice mặc định)
     * @return byte[] mảng byte âm thanh MP3
     */
    public byte[] generateSpeech(String text, String customVoiceId) {
        if (text == null || text.isBlank()) {
            throw new AppException(ErrorCode.TTS_TEXT_EMPTY);
        }

        if ("YOUR_ELEVENLABS_API_KEY".equals(apiKey)) {
            log.warn("Chưa cấu hình API Key thật cho ElevenLabs! Đang tạo file âm thanh mẫu (Mock Mode)...");
            return createMockMp3Bytes();
        }

        String voiceId = (customVoiceId != null && !customVoiceId.isBlank()) ? customVoiceId : defaultVoiceId;
        String url = "https://api.elevenlabs.io/v1/text-to-speech/" + voiceId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "audio/mpeg");

        Map<String, Object> body = new HashMap<>();
        body.put("text", text);
        body.put("model_id", modelId);

        Map<String, Object> voiceSettings = new HashMap<>();
        voiceSettings.put("stability", 0.5);
        voiceSettings.put("similarity_boost", 0.75);
        body.put("voice_settings", voiceSettings);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("Đang gọi ElevenLabs TTS API cho văn bản dài {} ký tự (Voice ID: {})...", text.length(), voiceId);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Gọi ElevenLabs TTS thành công, nhận về {} bytes audio MP3", response.getBody().length);
                return response.getBody();
            } else {
                throw new AppException(ErrorCode.TTS_API_FAILED);
            }
        } catch (Exception ex) {
            log.error("Lỗi khi gọi ElevenLabs TTS API: {}", ex.getMessage(), ex);
            throw new AppException(ErrorCode.TTS_API_FAILED);
        }
    }

    /**
     * Tạo mảng byte MP3 ngắn giả lập khi người dùng chưa điền API Key thật
     */
    private byte[] createMockMp3Bytes() {
        // Trả về header MP3 giả lập để không bị crash khi chưa có API key
        return new byte[]{
            (byte) 0xFF, (byte) 0xFB, (byte) 0x90, (byte) 0x44, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
    }
}
