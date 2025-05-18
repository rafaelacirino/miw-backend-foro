package es.upm.miw.foro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.foro.persistence.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    @Schema(hidden = true, description = "Unique identifier of the notification")
    private Long id;

    @NotNull(message = "User ID cannot be null")
    @Schema(description = "ID of the user receiving the notification", example = "1")
    private Long userId;

    @Schema(description = "Notification message to display", example = "Your question has been answered")
    private String message;

    @Schema(description = "ID of the related question", example = "1")
    private Long questionId;

    @Schema(description = "ID of the related answer", example = "1")
    private Long answerId;

    @Schema(hidden = true)
    private NotificationType type;

    @Schema(description = "Indicates if the notification has been read", example = "false")
    private boolean read;

    @Schema(description = "Date and time when the notification was created", example = "2023-10-01T10:05:00")
    private LocalDateTime creationDate;
}
