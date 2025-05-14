package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.NotificationDto;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Notification;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.User;

import java.util.List;

public interface NotificationService {

    void notifyNewAnswer(User user, Question question, Answer answer);

    void sendNotification(Notification notification);

    void markAsRead(Long notificationId);

    List<NotificationDto> getUserNotifications(Long userId);
}
