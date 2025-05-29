package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.service.email.EmailService;
import es.upm.miw.foro.service.email.PasswordResetEmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestConfig
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private PasswordResetEmailTemplate emailTemplate;

    @InjectMocks
    private EmailService emailService;

    private final String testEmail = "test@example.com";
    private final String resetLink = "http://localhost:4200/reset-password?token=abc123";
    private final String appName = "TestApp";

    @BeforeEach
    void setUp() {
        String fromEmail = "noreply@test.com";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailTemplate.buildResetEmailHtml(anyString())).thenReturn("<html><body>Mocked HTML</body></html>");
        emailService = new EmailService(emailTemplate, mailSender, fromEmail, appName);
    }

    @Test
    void sendPasswordResetEmail_Success() throws Exception {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendPasswordResetEmail(testEmail, resetLink);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_MessagingException_ThrowsRuntimeException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenAnswer(invocation -> {
            throw new MessagingException("Test exception");
        });

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetEmail(testEmail, resetLink);
        });

        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void sendPasswordResetEmail_MailException_ThrowsRuntimeException() throws Exception {
        // Arrange
        doThrow(new MailException("Test mail exception") {})
                .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetEmail(testEmail, resetLink);
        });
    }
}
