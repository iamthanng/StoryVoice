package fs.training.storyvoice.service;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String token);
}
