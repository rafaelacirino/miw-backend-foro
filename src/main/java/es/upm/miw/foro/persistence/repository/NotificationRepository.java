package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Notification;
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

    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.id IN :notificationIds")
    int markAsRead(@Param("userId") Long userId, @Param("notificationIds") List<Long> notificationIds);
}
