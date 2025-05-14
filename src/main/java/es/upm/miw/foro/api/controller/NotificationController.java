package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.NotificationDto;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.service.NotificationService;
import es.upm.miw.foro.service.UserService;
import es.upm.miw.foro.util.ApiPath;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public List<NotificationDto> getNotifications() {
        User user = userService.getAuthenticatedUser();
        return notificationService.getUserNotifications(user.getId());
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
