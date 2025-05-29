package es.upm.miw.foro.service.email;

import es.upm.miw.foro.exception.ServiceException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailService {

    private final PasswordResetEmailTemplate passwordResetEmailTemplate;
    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String appName;

    public EmailService(PasswordResetEmailTemplate passwordResetEmailTemplate, JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail,
                        @Value("${app.name}") String appName) {
        this.passwordResetEmailTemplate = passwordResetEmailTemplate;
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.appName = appName;
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, appName + " Support");
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request for " + appName);

            String htmlContent = passwordResetEmailTemplate.buildResetEmailHtml(resetLink); // usa o template aqui
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Error sending email to {}", toEmail, e);
            throw new ServiceException("Failed to send password reset email");
        }
    }
}