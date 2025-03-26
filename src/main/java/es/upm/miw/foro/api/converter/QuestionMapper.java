package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;

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

    public static Question toEntity(QuestionDto questionDto, User questionAuthor) {
        if (questionDto == null) {
            return null;
        }
        Question question = new Question();
        populateEntity(question, questionDto, questionAuthor);
        return question;
    }

    public static List<QuestionDto> toDtoList(List<Question> questions) {
        return questions.stream()
                .map(QuestionMapper::toQuestionDto)
                .collect(Collectors.toList());
    }

    public static List<Question> toEntityList(List<QuestionDto> questionDtos, User questionAuthor) {
        return questionDtos.stream()
                .map(dto -> QuestionMapper.toEntity(dto, questionAuthor))
                .collect(Collectors.toList());
    }

    private static void populateDto(Question question, QuestionDto dto) {
        dto.setId(question.getId());
        dto.setQuestionAuthor(question.getAuthor().getUserName());
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        dto.setCreationDate(question.getCreationDate());
        dto.setAnswers(AnswerMapper.toDtoList(question.getAnswers()));
    }

    private static void populateEntity(Question entity, QuestionDto questionDto, User author) {
        entity.setId(questionDto.getId());
        entity.setAuthor(author);
        entity.setTitle(questionDto.getTitle());
        entity.setDescription(questionDto.getDescription());
        entity.setCreationDate(questionDto.getCreationDate());
    }
}
