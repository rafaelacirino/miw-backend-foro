package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.EmailDto;
import es.upm.miw.foro.api.dto.ResetPasswordDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.email.PasswordResetService;
import es.upm.miw.foro.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class PasswordResetControllerTest {

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    private static final String EMAIL = "test@example.com";
    private static final String TOKEN = "test-token";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String ERROR_MESSAGE = "Test error message";

    @Test
    void testForgotPasswordSuccess() {
        // Arrange
        when(passwordResetService.sendPasswordResetEmail(EMAIL)).thenReturn(true);
        EmailDto emailDto = new EmailDto(EMAIL);

        // Act
        ResponseEntity<Object> response = passwordResetController.forgotPassword(emailDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(passwordResetService, times(1)).sendPasswordResetEmail(EMAIL);
    }

    @Test
    void testForgotPasswordServiceException() {
        // Arrange
        doThrow(new RuntimeException(ERROR_MESSAGE))
                .when(passwordResetService).sendPasswordResetEmail(EMAIL);

        EmailDto emailDto = new EmailDto(EMAIL);

        // Act
        ResponseEntity<Object> response = passwordResetController.forgotPassword(emailDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals(MessageUtil.UNEXPECTED_ERROR, ((Map<?, ?>) response.getBody()).get("message"));
        verify(passwordResetService, times(1)).sendPasswordResetEmail(EMAIL);
    }

    @Test
    void testResetPasswordSuccess() {
        // Arrange
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto(TOKEN, NEW_PASSWORD);

        // Act
        ResponseEntity<Object> response = passwordResetController.resetPassword(resetPasswordDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(passwordResetService, times(1)).resetPassword(TOKEN, NEW_PASSWORD);
    }

    @Test
    void testResetPasswordBadRequest() {
        // Arrange
        doThrow(new ServiceException(ERROR_MESSAGE))
                .when(passwordResetService).resetPassword(TOKEN, NEW_PASSWORD);

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto(TOKEN, NEW_PASSWORD);

        // Act
        ResponseEntity<Object> response = passwordResetController.resetPassword(resetPasswordDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ERROR_MESSAGE, response.getBody());
        verify(passwordResetService, times(1)).resetPassword(TOKEN, NEW_PASSWORD);
    }

    @Test
    void testResetPasswordInternalError() {
        // Arrange
        doThrow(new IllegalStateException(ERROR_MESSAGE))
                .when(passwordResetService).resetPassword(TOKEN, NEW_PASSWORD);

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto(TOKEN, NEW_PASSWORD);

        // Act
        ResponseEntity<Object> response = passwordResetController.resetPassword(resetPasswordDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals(MessageUtil.UNEXPECTED_ERROR, ((Map<?, ?>) response.getBody()).get("message"));
        verify(passwordResetService, times(1)).resetPassword(TOKEN, NEW_PASSWORD);
    }
}
