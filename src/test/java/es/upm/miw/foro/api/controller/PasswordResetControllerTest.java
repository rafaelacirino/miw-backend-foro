package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.impl.PasswordResetService;
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

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "test-token";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String ERROR_MESSAGE = "Test error message";

    @Test
    void testForgotPasswordSuccess() {
        // Act
        ResponseEntity<Object> response = passwordResetController.forgotPassword(TEST_EMAIL);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(passwordResetService, times(1)).sendPasswordResetEmail(TEST_EMAIL);
    }

    @Test
    void testForgotPasswordServiceException() {
        // Arrange
        doThrow(new RuntimeException(ERROR_MESSAGE))
                .when(passwordResetService).sendPasswordResetEmail(TEST_EMAIL);

        // Act
        ResponseEntity<Object> response = passwordResetController.forgotPassword(TEST_EMAIL);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals("An unexpected error occurred", ((Map<?, ?>) response.getBody()).get("message"));
        verify(passwordResetService, times(1)).sendPasswordResetEmail(TEST_EMAIL);
    }

    @Test
    void testResetPasswordSuccess() {
        // Act
        ResponseEntity<Object> response = passwordResetController.resetPassword(TEST_TOKEN, NEW_PASSWORD);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(passwordResetService, times(1)).resetPassword(TEST_TOKEN, NEW_PASSWORD);
    }

    @Test
    void testResetPasswordBadRequest() {
        // Arrange
        doThrow(new ServiceException(ERROR_MESSAGE))
                .when(passwordResetService).resetPassword(TEST_TOKEN, NEW_PASSWORD);

        // Act
        ResponseEntity<Object> response = passwordResetController.resetPassword(TEST_TOKEN, NEW_PASSWORD);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ERROR_MESSAGE, response.getBody());
        verify(passwordResetService, times(1)).resetPassword(TEST_TOKEN, NEW_PASSWORD);
    }

    @Test
    void testResetPasswordInternalError() {
        // Arrange
        doThrow(new IllegalStateException(ERROR_MESSAGE))
                .when(passwordResetService).resetPassword(TEST_TOKEN, NEW_PASSWORD);

        // Act
        ResponseEntity<Object> response = passwordResetController.resetPassword(TEST_TOKEN, NEW_PASSWORD);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals("An unexpected error occurred", ((Map<?, ?>) response.getBody()).get("message"));
        verify(passwordResetService, times(1)).resetPassword(TEST_TOKEN, NEW_PASSWORD);
    }
}
