package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.NotificationDto;
import es.upm.miw.foro.persistence.model.Notification;
import lombok.Generated;

import java.util.List;

public class NotificationMapper {

    @Generated
    private NotificationMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static NotificationDto toNotificationDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationDto notificationDto = new NotificationDto();
        populateDto(notification, notificationDto);
        return notificationDto;
    }

    public static Notification toEntity(NotificationDto notificationDto) {
        if (notificationDto == null) {
            return null;
        }
        Notification notification = new Notification();
        populateEntity(notification, notificationDto);
        return notification;
    }

    public static List<NotificationDto> toDtoList(List<Notification> notificationList) {
        return notificationList.stream()
                .map(NotificationMapper::toNotificationDto)
                .toList();
    }

    public static List<Notification> toEntityList(List<NotificationDto> notificationDtoList) {
        return notificationDtoList.stream()
                .map(NotificationMapper::toEntity)
                .toList();
    }

    private static void populateDto(Notification notification, NotificationDto notificationDto) {
        notificationDto.setId(notification.getId());
        notificationDto.setUserId(notification.getUser() != null ? notification.getUser().getId() : null);
        notificationDto.setMessage(generateMessage(notification));
        notificationDto.setQuestionId(notification.getQuestion() != null ? notification.getQuestion().getId() : null);
        notificationDto.setAnswerId(notification.getAnswer() != null ? notification.getAnswer().getId() : null);
        notificationDto.setType(notification.getType());
        notificationDto.setRead(notification.isRead());
        notificationDto.setCreationDate(notification.getCreationDate());
    }

    private static void populateEntity(Notification entity, NotificationDto notificationDto) {
        entity.setId(notificationDto.getId());
        entity.setType(notificationDto.getType());
        entity.setRead(notificationDto.isRead());
        entity.setCreationDate(notificationDto.getCreationDate());
    }

    private static String generateMessage(Notification notification) {
        return switch (notification.getType()) {
            case QUESTION_REPLIED -> "You have an answer";
            case ANSWER_RATED -> "Answer rated";
        };
    }
}
