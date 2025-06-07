package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.NotificationDto;
import es.upm.miw.foro.persistence.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class NotificationMapperTest {

    private static final Long NOTIFICATION_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long QUESTION_ID = 1L;
    private static final Long ANSWER_ID = 1L;
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2025, 1, 1, 10, 0);

    @Test
    void toNotificationDto_thenMapsDto() {
        // Arrange
        Notification notification = createNotification(NotificationType.QUESTION_REPLIED);

        // Act
        NotificationDto dto = NotificationMapper.toNotificationDto(notification);

        // Assert
        assertNotNull(dto);
        assertEquals(NOTIFICATION_ID, dto.getId());
        assertEquals(USER_ID, dto.getUserId());
        assertEquals("You have an answer", dto.getMessage());
        assertEquals(QUESTION_ID, dto.getQuestionId());
        assertEquals(ANSWER_ID, dto.getAnswerId());
        assertEquals(NotificationType.QUESTION_REPLIED, dto.getType());
        assertFalse(dto.isRead());
        assertEquals(CREATION_DATE, dto.getCreationDate());
    }

    @Test
    void toNotificationDtoWithNullNotification_thenReturnsNull() {
        // Act
        NotificationDto dto = NotificationMapper.toNotificationDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void toNotificationDtoWithDifferentNotificationTypes_ShouldGenerateCorrectMessages() {
        // Arrange
        Notification questionReplied = createNotification(NotificationType.QUESTION_REPLIED);
        Notification answerRated = createNotification(NotificationType.ANSWER_RATED);

        // Act
        NotificationDto dto1 = NotificationMapper.toNotificationDto(questionReplied);
        NotificationDto dto2 = NotificationMapper.toNotificationDto(answerRated);

        // Assert
        assertEquals("You have an answer", dto1.getMessage());
        assertEquals("Answer rated", dto2.getMessage());
    }

    @Test
    void toEntity_thenMapsToEntity() {
        // Arrange
        NotificationDto dto = NotificationDto.builder()
                .id(NOTIFICATION_ID)
                .type(NotificationType.ANSWER_RATED)
                .read(true)
                .creationDate(CREATION_DATE)
                .build();

        // Act
        Notification entity = NotificationMapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(NOTIFICATION_ID, entity.getId());
        assertEquals(NotificationType.ANSWER_RATED, entity.getType());
        assertTrue(entity.isRead());
        assertEquals(CREATION_DATE, entity.getCreationDate());
        assertNull(entity.getUser());
        assertNull(entity.getQuestion());
        assertNull(entity.getAnswer());
    }

    @Test
    void toEntityWithNullDto_thenReturnsNull() {
        // Act
        Notification entity = NotificationMapper.toEntity(null);

        // Assert
        assertNull(entity);
    }

    @Test
    void toDtoList_thenMapsToDtoList() {
        // Arrange
        List<Notification> notifications = List.of(
                createNotification(NotificationType.QUESTION_REPLIED),
                createNotification(NotificationType.ANSWER_RATED)
        );

        // Act
        List<NotificationDto> dtos = NotificationMapper.toDtoList(notifications);

        // Assert
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(NotificationType.QUESTION_REPLIED, dtos.get(0).getType());
        assertEquals(NotificationType.ANSWER_RATED, dtos.get(1).getType());
    }

    @Test
    void toDtoListWithEmptyList_thenReturnsEmptyList() {
        // Arrange
        List<Notification> notifications = Collections.emptyList();

        // Act
        List<NotificationDto> dtos = NotificationMapper.toDtoList(notifications);

        // Assert
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void toEntityList_shouldMapDtoListToEntityList() {
        // Arrange
        List<NotificationDto> dtos = List.of(
                NotificationDto.builder().type(NotificationType.QUESTION_REPLIED).build(),
                NotificationDto.builder().type(NotificationType.ANSWER_RATED).build()
        );

        // Act
        List<Notification> entities = NotificationMapper.toEntityList(dtos);

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals(NotificationType.QUESTION_REPLIED, entities.get(0).getType());
        assertEquals(NotificationType.ANSWER_RATED, entities.get(1).getType());
    }


    private Notification createNotification(NotificationType type) {
        User user = new User();
        user.setId(USER_ID);

        Question question = new Question();
        question.setId(QUESTION_ID);

        Answer answer = new Answer();
        answer.setId(ANSWER_ID);

        Notification notification = new Notification();
        notification.setId(NOTIFICATION_ID);
        notification.setUser(user);
        notification.setType(type);
        notification.setQuestion(question);
        notification.setAnswer(answer);
        notification.setRead(false);
        notification.setCreationDate(CREATION_DATE);

        return notification;
    }
}
