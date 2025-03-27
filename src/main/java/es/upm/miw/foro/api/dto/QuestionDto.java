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
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDto {

    @Schema(hidden = true, description = "Unique identifier of the question")
    private Long id;

    @NotNull(message = "Question Author cannot be null")
    @JsonProperty("author")
    @Schema(description = "Name of the user who created the question", example = "John Smith")
    private String author;

    @NotNull(message = "Title cannot be null")
    @Size(max = 60, message = "Title must be at most 60 characters")
    @Schema(description = "Title of the question", example = "How to implement JPA?")
    private String title;

    @Size(max = 150, message = "Description must be at most 150 characters")
    @Schema(description = "Brief description of the question", example = "I need help with JPA in Spring Boot")
    private String description;

    @Schema(hidden = true, description = "Date and time when the question was created", example = "2023-10-01T10:00:00")
    private LocalDateTime creationDate;

    @Schema(description = "List of answers to the question")
    private List<AnswerDto> answers= new ArrayList<>();
}
