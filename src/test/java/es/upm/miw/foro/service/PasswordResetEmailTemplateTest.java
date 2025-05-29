package es.upm.miw.foro.service;

import es.upm.miw.foro.service.email.PasswordResetEmailTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordResetEmailTemplateTest {

    private PasswordResetEmailTemplate template;

    @BeforeEach
    void setUp() {
        this.template = new PasswordResetEmailTemplate("TestApp");
    }

    @Test
    void shouldContainExpectedPlaceholders() {
        String resetLink = "http://localhost/reset?token=abc";
        String html = template.buildResetEmailHtml(resetLink);

        assertTrue(html.contains("Password Reset Request"));
        assertTrue(html.contains(resetLink));
        assertTrue(html.contains("TestApp"));
        assertTrue(html.contains(String.valueOf(LocalDate.now().getYear())));
    }
}
