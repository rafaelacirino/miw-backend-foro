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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDto {

    @Schema(hidden = true, description = "Unique identifier of the question")
    private Long id;

    @JsonProperty("author")
    @Schema(description = "Name of the user who created the question", example = "alex123")
    private String author;

    @NotNull(message = "Title cannot be null")
    @Size(max = 60, message = "Title must be at most 60 characters")
    @Schema(description = "Title of the question", example = "How to implement ...?")
    private String title;

    @Size(max = 150, message = "Description must be at most 150 characters")
    @Schema(description = "Brief description of the question", example = "I need help with ...")
    private String description;

    @Schema(hidden = true, description = "Date and time when the question was created", example = "2025-01-01T12:00:00")
    private LocalDateTime creationDate;

    @Schema(description = "List of answers to the question")
    private List<AnswerDto> answers= new ArrayList<>();

    @Size(max = 5, message = "Maximum 5 tags allowed")
    @Schema(description = "Set of tag names for the question")
    private Set<String> tags = new HashSet<>();

    @Schema(description = "Views for the question", example = "0")
    private Integer views;
}
