package es.upm.miw.foro.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PasswordResetEmailTemplate {

    private final String appName;

    public PasswordResetEmailTemplate(@Value("${app.name}") String appName) {
        this.appName = appName;
    }

    public String buildResetEmailHtml(String resetLink) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { max-width: 150px; }
                    .button {
                        display: inline-block; padding: 12px 24px; background-color: #2563eb;
                        color: white !important; text-decoration: none; border-radius: 4px;
                        font-weight: bold; margin: 20px 0;
                    }
                    .footer { margin-top: 30px; font-size: 12px; color: #666; text-align: center; }
                    .link { color: #2563eb; text-decoration: none; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>Password Reset Request</h2>
                </div>
                <p>We received a request to reset the password for your account.</p>
                <p>To proceed with the password reset, please click the button below:</p>
                <p style="text-align: center;">
                    <a href="%s" class="button">Reset Password</a>
                </p>
                <p>If the button doesn't work, copy and paste this link into your browser:</p>
                <p><a href="%s" class="link">%s</a></p>
                <p>If you didn't request this password reset, you can safely ignore this email.
                Your password will remain unchanged.</p>
                <div class="footer">
                    <p>For security reasons, this link will expire in 24 hours.</p>
                    <p>&copy; %s %d. All rights reserved.</p>
                </div>
            </body>
            </html>
            """.formatted(
                resetLink,
                resetLink,
                resetLink,
                appName,
                LocalDate.now().getYear()
        );
    }
}
