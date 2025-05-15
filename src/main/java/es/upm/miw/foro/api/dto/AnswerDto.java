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
    @Size(max = 1000, message = "Content must be at most 1000 characters")
    @Schema(description = "Content of the answer", example = "The answer is ...")
    private String content;

//    @NotNull(message = "Unique identifier of the question")
    private Long questionId;

//    @NotNull(message = "AnswerAuthor cannot be null")
    @JsonProperty("author")
    @Schema(description = "Name of the user who wrote the answer", example = "me123")
    private String author;

    @Schema(hidden = true, description = "Date and time when the answer was created", example = "2025-01-01T10:00:00")
    private LocalDateTime creationDate;
}
