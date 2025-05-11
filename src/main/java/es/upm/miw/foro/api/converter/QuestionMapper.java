package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.User;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
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

        try {
            return questionDto;
        } catch (ConstraintViolationException e) {
            log.error("DTO validation failed for question {}: {}", question.getId(), e.getMessage());
            return null;
        }
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
        if (questions == null) {
            return Collections.emptyList();
        }
        return questions.stream()
                .map(QuestionMapper::toQuestionDto)
                .toList();
    }

    public static List<Question> toEntityList(List<QuestionDto> questionDtos, User questionAuthor) {
        if (questionDtos == null) {
            return Collections.emptyList();
        }
        return questionDtos.stream()
                .map(dto -> toEntity(dto, questionAuthor))
                .toList();
    }

    private static void populateDto(Question question, QuestionDto dto) {
        if (question == null || dto == null) {
            return;
        }

        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        dto.setCreationDate(question.getCreationDate());
        dto.setViews(question.getViews() != null ? question.getViews() : 0);

        try {
            String authorName = Optional.ofNullable(question.getAuthor())
                    .map(User::getUserName)
                    .orElse("Unknown");
            dto.setAuthor(authorName);
        } catch (Exception e) {
            log.error("Error mapping author for question {}: {}", question.getId(), e.getMessage());
            dto.setAuthor("Unknown");
        }

        dto.setAnswers(
                question.getAnswers() != null
                        ? AnswerMapper.toDtoList(question.getAnswers())
                        : null
        );
    }

    private static void populateEntity(Question question, QuestionDto questionDto, User questionAuthor) {
        question.setId(questionDto.getId());
        question.setAuthor(questionAuthor);
        question.setTitle(questionDto.getTitle());
        question.setDescription(questionDto.getDescription());
        question.setCreationDate(questionDto.getCreationDate());
        question.setViews(questionDto.getViews() != null ? questionDto.getViews() : 0);

        if (questionDto.getAnswers() != null) {
            List<Answer> answers = AnswerMapper.toEntityList(questionDto.getAnswers(), question, questionAuthor);
            answers.forEach(question::addAnswer);
        }
    }
}
