package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.converter.NotificationMapper;
import es.upm.miw.foro.api.dto.NotificationDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.*;
import es.upm.miw.foro.persistence.repository.NotificationRepository;
import es.upm.miw.foro.service.impl.NotificationServiceImpl;
import es.upm.miw.foro.util.ApiPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestConfig
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User testUser;
    private Question testQuestion;
    private Answer testAnswer;
    private Notification testNotification;
    private static final Long USER_ID = 1L;
    private static final Long QUESTION_ID = 1L;
    private static final Long ANSWER_ID = 1L;
    private static final Long NOTIFICATION_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setEmail("user@example.com");

        testQuestion = new Question();
        testQuestion.setId(QUESTION_ID);
        testQuestion.setTitle("Test Question");

        testAnswer = new Answer();
        testAnswer.setId(ANSWER_ID);
        testAnswer.setContent("Test Answer");

        testNotification = new Notification();
        testNotification.setId(NOTIFICATION_ID);
        testNotification.setUser(testUser);
        testNotification.setQuestion(testQuestion);
        testNotification.setAnswer(testAnswer);
        testNotification.setType(NotificationType.QUESTION_REPLIED);
        testNotification.setCreationDate(LocalDateTime.now());
        testNotification.setRead(false);
    }

    @Test
    void testNotifyNewAnswer() {
        // Arrange
        NotificationDto expectedDto = NotificationMapper.toNotificationDto(testNotification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        notificationService.notifyNewAnswer(testUser, testQuestion, testAnswer);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(messagingTemplate, times(1))
                .convertAndSend(ApiPath.TOPIC_NOTIFICATIONS + USER_ID, expectedDto);
    }

    @Test
    void testNotifyNewAnswerSetNotificationType() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            assertEquals(NotificationType.QUESTION_REPLIED, n.getType());
            return testNotification;
        });

        // Act
        notificationService.notifyNewAnswer(testUser, testQuestion, testAnswer);

        // Verify
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testNotifyNewAnswerSetUnreadAndCurrentDate() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            assertFalse(n.isRead());
            assertNotNull(n.getCreationDate());
            return testNotification;
        });

        // Act
        notificationService.notifyNewAnswer(testUser, testQuestion, testAnswer);

        // Verify
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testSendNotification() {
        // Arrange
        Notification notification = new Notification();
        notification.setUser(testUser);
        notification.setType(NotificationType.ANSWER_RATED);

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            assertFalse(n.isRead());
            assertNotNull(n.getCreationDate());
            return testNotification;
        });

        // Act
        notificationService.sendNotification(notification);

        // Assert
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testSendNotificationNotSendWebSocketMessage() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        notificationService.sendNotification(testNotification);

        // Assert
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testMarkAsRead() {
        // Arrange
        Notification unreadNotification = new Notification();
        unreadNotification.setRead(false);

        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(unreadNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            assertTrue(n.isRead());
            return n;
        });

        // Act
        notificationService.markAsRead(NOTIFICATION_ID);

        // Assert
        verify(notificationRepository, times(1)).findById(NOTIFICATION_ID);
        verify(notificationRepository, times(1)).save(unreadNotification);
    }

    @Test
    void testMarkAsReadWithNonExistingNotification_DoNothing() {
        // Arrange
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.empty());

        // Act
        notificationService.markAsRead(NOTIFICATION_ID);

        // Assert
        verify(notificationRepository, times(1)).findById(NOTIFICATION_ID);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetUserNotifications() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        when(notificationRepository.findByUserIdOrderByCreationDateDesc(USER_ID)).thenReturn(notifications);

        // Act
        List<NotificationDto> result = notificationService.getUserNotifications(USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NOTIFICATION_ID, result.getFirst().getId());
        verify(notificationRepository, times(1)).findByUserIdOrderByCreationDateDesc(USER_ID);
    }

    @Test
    void testGetUserNotificationsRepositoryException() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByCreationDateDesc(USER_ID))
                .thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(RepositoryException.class, () -> {
            notificationService.getUserNotifications(USER_ID);
        });
    }

    @Test
    void testGetUserNotificationsServiceException() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByCreationDateDesc(USER_ID))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThrows(ServiceException.class, () -> {
            notificationService.getUserNotifications(USER_ID);
        });
    }

    @Test
    void testGetUserNotificationsEmptyList() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByCreationDateDesc(USER_ID)).thenReturn(Collections.emptyList());

        // Act
        List<NotificationDto> result = notificationService.getUserNotifications(USER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
