package fs.training.storyvoice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import fs.training.storyvoice.service.FileStorageService;
import fs.training.storyvoice.exception.AppException;
import fs.training.storyvoice.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    /**
     * Lưu file ảnh bìa truyện lên Cloudinary (folder: storyvoice/covers)
     * Có áp dụng tối ưu hóa ảnh tự động (f_auto, q_auto) theo chuẩn Cloudinary Best Practices
     * @return đường dẫn tuyệt đối (secure URL đã được optimize)
     */
    public String storeCoverImage(MultipartFile file) {
        return storeFileToCloudinary(file, "storyvoice/covers", true);
    }

    /**
     * Lưu file audio thu sẵn lên Cloudinary (folder: storyvoice/audio)
     * @return đường dẫn tuyệt đối (secure URL)
     */
    public String storeAudioFile(MultipartFile file) {
        return storeFileToCloudinary(file, "storyvoice/audio", false);
    }

    /**
     * Lưu file từ mảng bytes (phục vụ lưu file MP3 do API TTS sinh ra) lên Cloudinary
     * @return đường dẫn tuyệt đối (secure URL)
     */
    public String storeTtsAudioBytes(byte[] audioBytes, String filename) {
        try {
            // Loại bỏ phần mở rộng .mp3 để dùng làm public_id (Cloudinary tự động thêm đuôi)
            String publicId = filename;
            if (filename.contains(".")) {
                publicId = filename.substring(0, filename.lastIndexOf('.'));
            }
            
            Map<?, ?> uploadResult = cloudinary.uploader().upload(audioBytes, ObjectUtils.asMap(
                    "resource_type", "video", // Audio trong Cloudinary dùng resource_type = video
                    "folder", "storyvoice/audio/tts",
                    "public_id", publicId
            ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException ex) {
            log.error("Lỗi khi upload TTS audio lên Cloudinary: {}", ex.getMessage(), ex);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private String storeFileToCloudinary(MultipartFile file, String folder, boolean isImageOptimization) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        try {
            // Xác định resource_type dựa trên loại file (auto sẽ tự động nhận diện image/video/raw)
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "auto",
                    "folder", folder,
                    "public_id", UUID.randomUUID().toString()
            ));
            
            String secureUrl = uploadResult.get("secure_url").toString();
            
            // Nếu là ảnh (cover) và cần optimize, chèn f_auto,q_auto vào đường dẫn URL
            if (isImageOptimization && secureUrl.contains("/image/upload/")) {
                secureUrl = secureUrl.replace("/image/upload/", "/image/upload/f_auto,q_auto/");
            }
            
            return secureUrl;
        } catch (IOException ex) {
            log.error("Không thể upload file lên Cloudinary: {}", ex.getMessage(), ex);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Xóa file trên Cloudinary khi cập nhật hoặc xóa Entity
     * @param fileUrl đường dẫn tuyệt đối của file trên Cloudinary
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank() || !fileUrl.contains("res.cloudinary.com")) {
            return;
        }
        try {
            // Lấy public_id từ URL
            // Ví dụ: https://res.cloudinary.com/dzrtrxxqk/image/upload/v1723821033/storyvoice/covers/abc.jpg
            // public_id sẽ là "storyvoice/covers/abc"
            String publicId = extractPublicId(fileUrl);
            
            if (publicId != null) {
                // Determine resource type (image vs video(audio)) based on URL
                String resourceType = fileUrl.contains("/video/upload/") ? "video" : "image";
                cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
                log.info("Đã xóa file trên Cloudinary: {}", publicId);
            }
        } catch (IOException ex) {
            log.warn("Không thể xóa file {} trên Cloudinary: {}", fileUrl, ex.getMessage());
        }
    }
    
    private String extractPublicId(String fileUrl) {
        try {
            // Tách bằng /upload/
            String[] parts = fileUrl.split("/upload/");
            if (parts.length < 2) return null;
            
            // Bỏ qua phần version (v1234567/) nếu có
            String pathAfterUpload = parts[1];
            if (pathAfterUpload.matches("f_auto,q_auto/.*")) {
                pathAfterUpload = pathAfterUpload.replaceFirst("f_auto,q_auto/", "");
            }
            if (pathAfterUpload.matches("v\\d+/.*")) {
                pathAfterUpload = pathAfterUpload.replaceFirst("v\\d+/", "");
            }
            
            // Xoá phần đuôi file (.jpg, .png, .mp3)
            int lastDotIndex = pathAfterUpload.lastIndexOf('.');
            if (lastDotIndex != -1) {
                pathAfterUpload = pathAfterUpload.substring(0, lastDotIndex);
            }
            return pathAfterUpload;
        } catch (Exception e) {
            log.warn("Lỗi khi tách public_id từ URL {}: {}", fileUrl, e.getMessage());
            return null;
        }
    }
}
