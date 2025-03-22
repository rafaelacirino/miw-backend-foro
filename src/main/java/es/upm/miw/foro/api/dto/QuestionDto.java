package es.upm.miw.foro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class QuestionDto {

    @Schema(hidden = true)
    private Long id;

    @NotNull(message = "Author cannot be null")
    @Size(max = 15, message = "Author must be at most 15 characters")
    private String author;

    @NotNull(message = "Title cannot be null")
    @Size(max = 60, message = "Title must be at most 60 characters")
    private String title;

    @Size(max = 150, message = "Description must be at most 150 characters")
    private String description;

    @Schema(hidden = true)
    private LocalDateTime creationDate;

    private Boolean wasRead;
}
