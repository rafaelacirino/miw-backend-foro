package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.NotificationDto;
import es.upm.miw.foro.persistence.model.NotificationType;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.service.NotificationService;
import es.upm.miw.foro.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestConfig
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;
    private NotificationDto notificationDto;
    private static final Long ID = 1L;
    private static final String USER_EMAIL = "user@email.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(ID);
        testUser.setEmail(USER_EMAIL);
        notificationDto = NotificationDto.builder()
                .id(ID)
                .userId(ID)
                .message("Your question has been answered")
                .questionId(ID)
                .answerId(ID)
                .type(NotificationType.QUESTION_REPLIED)
                .read(false)
                .creationDate(LocalDateTime.now())
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userService.getAuthenticatedUser()).thenReturn(testUser);
    }

    @Test
    void testGetNotifications() {
        // Arrange
        List<NotificationDto> notifications = List.of(notificationDto);
        when(notificationService.getUserNotifications(anyLong())).thenReturn(notifications);

        // Act
        List<NotificationDto> result = notificationController.getNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(notificationDto, result.getFirst());
        verify(notificationService, times(1)).getUserNotifications(testUser.getId());
    }

    @Test
    void testGetNotificationsEmptyList() {
        // Arrange
        when(notificationService.getUserNotifications(anyLong())).thenReturn(Collections.emptyList());

        // Act
        List<NotificationDto> result = notificationController.getNotifications();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMarkAsRead() {
        // Arrange
        doNothing().when(notificationService).markAsRead(anyLong());

        // Act
        notificationController.markAsRead(1L);

        // Assert
        verify(notificationService, times(1)).markAsRead(1L);
    }
}
