package fs.training.storyvoice.service;

public interface TextToSpeechService {
    byte[] generateSpeech(String text, String customVoiceId);
}
