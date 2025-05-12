package es.upm.miw.foro.persistance.repository;

import es.upm.miw.foro.persistance.model.Notification;
import es.upm.miw.foro.persistance.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreationDateDesc(Long userId);

    // Buscar notificaciones no leídas por usuario
//    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);
//
//    List<Notification> findByRecipientIdAndTypeOrderByCreatedAtDesc(Long recipientId, NotificationType type);

    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.id IN :notificationIds")
    int markAsRead(@Param("userId") Long userId, @Param("notificationIds") List<Long> notificationIds);
}
