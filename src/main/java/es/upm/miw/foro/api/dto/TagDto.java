package es.upm.miw.foro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagDto {

    @Schema(hidden = true, description = "Unique identifier of the tag")
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(max = 20, message = "Name must be at most 20 characters")
    @Schema(description = "Tag name of the question", example = "Bug")
    private String name;

    @Schema(description = "List of questions to the tag")
    private List<QuestionDto> questions = new ArrayList<>();
}
