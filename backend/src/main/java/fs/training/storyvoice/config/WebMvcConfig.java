package fs.training.storyvoice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = resolveUploadPath(uploadDir);
        String uploadAbsolutePath = uploadPath.toUri().toString();
        if (!uploadAbsolutePath.endsWith("/")) {
            uploadAbsolutePath += "/";
        }

        // Đấu nối URL /uploads/** tới thư mục đĩa cứng local ./uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadAbsolutePath);
    }

    public static Path resolveUploadPath(String configuredDir) {
        Path backendPath = Paths.get("backend/uploads").toAbsolutePath().normalize();
        if (Files.exists(backendPath)) {
            return backendPath;
        }
        return Paths.get(configuredDir != null ? configuredDir : "./uploads").toAbsolutePath().normalize();
    }
}
