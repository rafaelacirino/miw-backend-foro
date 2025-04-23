package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        if (questions == null) {
            return Collections.emptyList();
        }
        return questions.stream()
                .map(QuestionMapper::toQuestionDto)
                .collect(Collectors.toList());
    }

    public static List<Question> toEntityList(List<QuestionDto> questionDtos, User questionAuthor) {
        if (questionDtos == null) {
            return Collections.emptyList();
        }
        return questionDtos.stream()
                .map(dto -> toEntity(dto, questionAuthor))
                .collect(Collectors.toList());
    }

    private static void populateDto(Question question, QuestionDto dto) {
        dto.setId(question.getId());
        //dto.setAuthor(question.getAuthor().getUserName());
        if (question.getAuthor() != null) {
            String userName = question.getAuthor().getUserName();
            if (userName != null) {
                dto.setAuthor(userName);
                log.info("Mapped author for question {}: {}", question.getId(), userName);
            } else {
                log.warn("userName is null for author of question {}", question.getId());
                dto.setAuthor("Unknown");
            }
        } else {
            log.warn("Author is null for question {}", question.getId());
            dto.setAuthor("Unknown");
        }
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        dto.setCreationDate(question.getCreationDate());
        dto.setAnswers(AnswerMapper.toDtoList(question.getAnswers()));
    }

    private static void populateEntity(Question question, QuestionDto questionDto, User questionAuthor) {
        question.setId(questionDto.getId());
        question.setAuthor(questionAuthor);
        question.setTitle(questionDto.getTitle());
        question.setDescription(questionDto.getDescription());
        question.setCreationDate(questionDto.getCreationDate());

        if (questionDto.getAnswers() != null) {
            List<Answer> answers = AnswerMapper.toEntityList(questionDto.getAnswers(), question);
            answers.forEach(question::addAnswer);
        }
    }
}
