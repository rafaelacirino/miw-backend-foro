package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.NotificationMapper;
import es.upm.miw.foro.api.dto.NotificationDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.*;
import es.upm.miw.foro.persistance.repository.NotificationRepository;
import es.upm.miw.foro.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public void notifyNewAnswer(User user, Question question, Answer answer) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setQuestion(question);
        notification.setAnswer(answer);
        notification.setType(NotificationType.QUESTION_REPLIED);
        notification.setCreationDate(LocalDateTime.now());
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);

        NotificationDto dto = NotificationMapper.toNotificationDto(savedNotification);

        messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), dto);
    }

    @Override
    @Transactional
    public void sendNotification(Notification notification) {
        notification.setCreationDate(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notif -> {
                    notif.setRead(true);
                    notificationRepository.save(notif);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        try {
            log.info("Fetching notifications for user ID: {}", userId);

            List<Notification> notifications = notificationRepository.findByUserIdOrderByCreationDateDesc(userId);

            notifications.forEach(notification -> {
                Hibernate.initialize(notification.getUser());
                Hibernate.initialize(notification.getQuestion());
                Hibernate.initialize(notification.getAnswer());
            });

            return NotificationMapper.toDtoList(notifications);

        } catch (DataAccessException exception) {
            log.error("Error while getting notifications for user ID: {}", userId, exception);
            throw new RepositoryException("Error while getting notifications", exception);
        } catch (Exception exception) {
            log.error("Unexpected error while getting notifications for user ID: {}", userId, exception);
            throw new ServiceException("Unexpected error while getting notifications", exception);
        }
    }
}