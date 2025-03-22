package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.persistance.model.Question;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {

    public QuestionMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static QuestionDto toQuestionDto(Question question) {
        if (question == null) {
            return null;
        }
        QuestionDto questionDto = new QuestionDto();
        populateDto(question, questionDto);
        return questionDto;
    }

    public static Question toEntity(QuestionDto questionDto) {
        if (questionDto == null) {
            return null;
        }
        Question question = new Question();
        populateEntity(question, questionDto);
        return question;
    }

    public static List<QuestionDto> toDtoList(List<Question> questions) {
        return questions.stream()
                .map(QuestionMapper::toQuestionDto)
                .collect(Collectors.toList());
    }

    public static List<Question> toEntityList(List<QuestionDto> questionDtos) {
        return questionDtos.stream()
                .map(QuestionMapper::toEntity)
                .collect(Collectors.toList());
    }

    private static void populateDto(Question question, QuestionDto dto) {
        dto.setId(question.getId());
        dto.setAuthor(question.getAuthor());
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        dto.setCreationDate(question.getCreationDate());
        dto.setWasRead(question.getWasRead());
    }

    private static void populateEntity(Question entity, QuestionDto questionDto) {
        entity.setId(questionDto.getId());
        entity.setAuthor(questionDto.getAuthor());
        entity.setTitle(questionDto.getTitle());
        entity.setDescription(questionDto.getDescription());
        entity.setCreationDate(questionDto.getCreationDate());
        entity.setWasRead(questionDto.getWasRead());
    }
}
