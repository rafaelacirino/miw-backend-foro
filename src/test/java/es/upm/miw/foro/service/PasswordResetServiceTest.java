package es.upm.miw.foro.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import es.upm.miw.foro.service.impl.EmailService;
import es.upm.miw.foro.service.impl.JwtService;
import es.upm.miw.foro.service.impl.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestConfig
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "test-token";
    private static final String NEW_PASSWORD = "newPassword123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String RESET_LINK_DEV = "http://localhost:4200/reset-password?token=" + TEST_TOKEN;
    private static final String RESET_LINK_PROD = "https://capturing-forum.onrender.com/reset-password?token=" + TEST_TOKEN;

    private UserDto userDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setEmail(TEST_EMAIL);
        userDto.setPassword("oldPassword");

        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword("oldPassword");

        ReflectionTestUtils.setField(passwordResetService, "environment", "dev");
    }

    @Test
    void sendPasswordResetEmail_UserExists_SendsEmail() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(jwtService.createPasswordResetToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

        // Act
        passwordResetService.sendPasswordResetEmail(userDto.getEmail());

        // Assert
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(jwtService, times(1)).createPasswordResetToken(TEST_EMAIL);
        verify(emailService, times(1)).sendPasswordResetEmail(TEST_EMAIL, RESET_LINK_DEV);
    }

    @Test
    void sendPasswordResetEmail_UserNotExists_LogsInfo() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act
        passwordResetService.sendPasswordResetEmail(userDto.getEmail());

        // Assert
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(jwtService, never()).createPasswordResetToken(anyString());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void resetPassword_ValidToken_UpdatesPassword() {
        // Arrange
        when(jwtService.validatePasswordResetToken(TEST_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        // Act
        passwordResetService.resetPassword(TEST_TOKEN, NEW_PASSWORD);

        // Assert
        verify(jwtService, times(1)).validatePasswordResetToken(TEST_TOKEN);
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
        verify(userRepository, times(1)).save(testUser);
        assertEquals(ENCODED_PASSWORD, testUser.getPassword());
    }

    @Test
    void resetPassword_InvalidToken_ThrowsServiceException() {
        // Arrange
        when(jwtService.validatePasswordResetToken(TEST_TOKEN)).thenThrow(new JWTVerificationException("Invalid token"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            passwordResetService.resetPassword(TEST_TOKEN, NEW_PASSWORD);
        });
        assertEquals("Invalid or expired token", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void resetPassword_UserNotFound_ThrowsServiceException() {
        // Arrange
        when(jwtService.validatePasswordResetToken(TEST_TOKEN)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            passwordResetService.resetPassword(TEST_TOKEN, NEW_PASSWORD);
        });
        assertEquals("User not found", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void sendPasswordResetEmail_InProduction_UsesProductionUrl() {
        // Arrange
        passwordResetService = new PasswordResetService(userRepository, jwtService, emailService, passwordEncoder);
        passwordResetService.environment = "prod";

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(jwtService.createPasswordResetToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

        // Act
        passwordResetService.sendPasswordResetEmail(TEST_EMAIL);

        // Assert
        verify(emailService, times(1)).sendPasswordResetEmail(TEST_EMAIL, RESET_LINK_PROD);
    }
}
