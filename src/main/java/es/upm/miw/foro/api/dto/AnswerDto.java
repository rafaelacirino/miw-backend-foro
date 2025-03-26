package es.upm.miw.foro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnswerDto {

    @Schema(hidden = true, description = "Unique identifier of the answer")
    private Long id;

    @NotNull(message = "Content cannot be null")
    @Size(max = 500, message = "Content must be at most 500 characters")
    @Schema(description = "Content of the answer", example = "Use @Entity and @Id annotations...")
    private String content;

    @NotNull(message = "AnswerAuthor cannot be null")
    @Schema(description = "Name of the user who wrote the answer", example = "Mary Smith")
    @JsonProperty("author")
    private String answerAuthor;

    @Schema(description = "Date and time when the answer was created", example = "2023-10-01T10:05:00")
    private LocalDateTime createdDate;
}
