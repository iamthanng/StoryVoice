package fs.training.storyvoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path uploadLocation;

    public FileStorageService(@Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
            Files.createDirectories(this.uploadLocation.resolve("covers"));
            Files.createDirectories(this.uploadLocation.resolve("audio"));
            Files.createDirectories(this.uploadLocation.resolve("audio/tts"));
            log.info("Thư mục lưu trữ file được khởi tạo tại: {}", this.uploadLocation);
        } catch (Exception ex) {
            log.error("Không thể tạo thư mục lưu trữ file: {}", ex.getMessage(), ex);
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file", ex);
        }
    }

    /**
     * Lưu file ảnh bìa truyện vào subfolder /covers
     * @return đường dẫn tương đối (ví dụ: "/uploads/covers/uuid_filename.jpg")
     */
    public String storeCoverImage(MultipartFile file) {
        return storeFile(file, "covers");
    }

    /**
     * Lưu file audio thu sẵn vào subfolder /audio
     * @return đường dẫn tương đối (ví dụ: "/uploads/audio/uuid_filename.mp3")
     */
    public String storeAudioFile(MultipartFile file) {
        return storeFile(file, "audio");
    }

    /**
     * Lưu file từ mảng bytes (phục vụ lưu file MP3 do API TTS sinh ra)
     * @return đường dẫn tương đối (ví dụ: "/uploads/audio/tts/chapter_1_timestamp.mp3")
     */
    public String storeTtsAudioBytes(byte[] audioBytes, String filename) {
        try {
            Path targetDir = this.uploadLocation.resolve("audio/tts");
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(filename);
            Files.write(targetLocation, audioBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return "/uploads/audio/tts/" + filename;
        } catch (IOException ex) {
            log.error("Lỗi khi lưu file TTS audio: {}", ex.getMessage(), ex);
            throw new RuntimeException("Không thể lưu file TTS audio", ex);
        }
    }

    private String storeFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = "";
        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex >= 0) {
            fileExtension = originalFilename.substring(extensionIndex);
        }

        String storedFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.uploadLocation.resolve(subDir).resolve(storedFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + subDir + "/" + storedFilename;
        } catch (IOException ex) {
            log.error("Không thể lưu file {}: {}", storedFilename, ex.getMessage(), ex);
            throw new RuntimeException("Không thể lưu file: " + originalFilename, ex);
        }
    }

    /**
     * Xóa file khi cập nhật hoặc xóa Entity
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            // Giả sử relativePath có dạng "/uploads/covers/xxx.jpg" -> cắt "/uploads/"
            String cleanPath = relativePath.startsWith("/uploads/") 
                    ? relativePath.substring("/uploads/".length()) 
                    : relativePath;
            
            Path filePath = this.uploadLocation.resolve(cleanPath).normalize();
            Files.deleteIfExists(filePath);
            log.info("Đã xóa file: {}", filePath);
        } catch (IOException ex) {
            log.warn("Không thể xóa file {}: {}", relativePath, ex.getMessage());
        }
    }
}
