package fs.training.storyvoice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeCoverImage(MultipartFile file);
    String storeAudioFile(MultipartFile file);
    String storeTtsAudioBytes(byte[] audioBytes, String filename);
    void deleteFile(String fileUrl);
}
