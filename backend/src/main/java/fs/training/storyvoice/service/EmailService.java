package fs.training.storyvoice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Gửi email chứa link đặt lại mật khẩu.
     *
     * @param toEmail   Email người nhận
     * @param token     Token đặt lại mật khẩu (UUID)
     */
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[StoryVoice] Đặt lại mật khẩu của bạn");
        message.setText(
                "Xin chào!\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản StoryVoice.\n\n" +
                "Nhấp vào link bên dưới để đặt lại mật khẩu (hết hạn sau 15 phút):\n\n" +
                resetLink + "\n\n" +
                "Nếu bạn không yêu cầu điều này, hãy bỏ qua email này.\n\n" +
                "Trân trọng,\nĐội ngũ StoryVoice"
        );

        try {
            mailSender.send(message);
            log.info("Đã gửi email đặt lại mật khẩu tới: {}", toEmail);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email tới {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }
}
