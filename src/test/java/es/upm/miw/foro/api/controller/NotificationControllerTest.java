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

    @BeforeEach
    void setUp() {
        notificationDto = NotificationDto.builder()
                .id(1L)
                .userId(1L)
                .message("Your question has been answered")
                .questionId(1L)
                .answerId(1L)
                .type(NotificationType.QUESTION_REPLIED)
                .read(false)
                .creationDate(LocalDateTime.now())
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
    }

//    @Test
//    void testGetNotifications() {
//        // Arrange
//        List<NotificationDto> notifications = List.of(notificationDto);
//        when(notificationService.getUserNotifications(anyLong())).thenReturn(notifications);
//
//        // Act
//        List<NotificationDto> result = notificationController.getNotifications();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(notificationDto, result.getFirst());
//        verify(notificationService, times(1)).getUserNotifications(testUser.getId());
//    }
//
//    @Test
//    void testGetNotificationsEmptyList() {
//        // Arrange
//        when(notificationService.getUserNotifications(anyLong())).thenReturn(Collections.emptyList());
//
//        // Act
//        List<NotificationDto> result = notificationController.getNotifications();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }

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
