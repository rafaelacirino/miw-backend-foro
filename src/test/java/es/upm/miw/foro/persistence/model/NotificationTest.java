package es.upm.miw.foro.persistence.model;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.persistance.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class NotificationTest {

    @Test
    void testNoArgsConstructor() {
        // Act
        Notification notification = new Notification();

        // Assert
        assertNull(notification.getId());
        assertNull(notification.getUser());
        assertNull(notification.getQuestion());
        assertNull(notification.getAnswer());
        assertNull(notification.getType());
        assertFalse(notification.isRead());
        assertNull(notification.getCreationDate());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        User user = Mockito.mock(User.class);
        Question question = Mockito.mock(Question.class);
        Answer answer = Mockito.mock(Answer.class);
        NotificationType type = NotificationType.ANSWER_RATED;
        boolean isRead = false;
        LocalDateTime creationDate = LocalDateTime.now();

        // Act
        Notification notification = new Notification(
                1L, user, question, answer, type, isRead, creationDate
        );

        // Assert
        assertEquals(1L, notification.getId());
        assertEquals(user, notification.getUser());
        assertEquals(question, notification.getQuestion());
        assertEquals(answer, notification.getAnswer());
        assertEquals(type, notification.getType());
        assertFalse(notification.isRead());
        assertEquals(creationDate, notification.getCreationDate());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Notification notification = new Notification();
        User user = Mockito.mock(User.class);
        Question question = Mockito.mock(Question.class);
        Answer answer = Mockito.mock(Answer.class);
        NotificationType type = NotificationType.ANSWER_RATED;
        LocalDateTime creationDate = LocalDateTime.now();

        // Act
        notification.setId(100L);
        notification.setUser(user);
        notification.setQuestion(question);
        notification.setAnswer(answer);
        notification.setType(type);
        notification.setRead(true);
        notification.setCreationDate(creationDate);

        // Assert
        assertEquals(100L, notification.getId());
        assertEquals(user, notification.getUser());
        assertEquals(question, notification.getQuestion());
        assertEquals(answer, notification.getAnswer());
        assertEquals(type, notification.getType());
        assertTrue(notification.isRead());
        assertEquals(creationDate, notification.getCreationDate());
    }

    @Test
    void testOnCreate_setsCreationDate() {
        // Arrange
        Notification notification = new Notification();
        assertNull(notification.getCreationDate());

        // Act
        notification.onCreate();

        // Assert
        assertNotNull(notification.getCreationDate());
        assertTrue(notification.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(notification.getCreationDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testMarkAsRead_setsIsReadToTrue() {
        // Arrange
        Notification notification = new Notification();
        assertFalse(notification.isRead());

        // Act
        notification.markAsRead();

        // Assert
        assertTrue(notification.isRead());
    }
}
