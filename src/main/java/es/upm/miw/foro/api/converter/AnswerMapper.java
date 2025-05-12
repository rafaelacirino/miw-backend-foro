package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.User;

import java.util.Collections;
import java.util.List;

public class AnswerMapper {

    public AnswerMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Converts an Answer entity to an AnswerDto.
     *
     * @param answer the Answer entity to convert
     * @return the corresponding AnswerDto, or null if the input is null
     */
    public static AnswerDto toAnswerDto(Answer answer) {
        if (answer == null) {
            return null;
        }
        AnswerDto answerDto = new AnswerDto();
        populateDto(answer, answerDto);
        return answerDto;
    }

    /**
     * Converts an AnswerDto to an entity Answer.
     *
     * @param answerDto the AnswerDto to convert
     * @return the corresponding Answer, or null if the input is null
     */
    public static Answer toEntity(AnswerDto answerDto, Question question, User author) {
        if (answerDto == null) {
            return null;
        }
        Answer answer = new Answer();
        populateEntity(answer, answerDto, question, author);
        return answer;
    }

    public static List<AnswerDto> toDtoList(List<Answer> answers) {
        if (answers == null) {
            return Collections.emptyList();
        }
        return answers.stream()
                .map(AnswerMapper::toAnswerDto)
                .toList();
    }

    public static List<Answer> toEntityList(List<AnswerDto> answerDtos, Question question, User author) {
        return answerDtos.stream()
                .map(dto -> toEntity(dto, question, author))
                .toList();
    }

    public static void populateDto(Answer answer, AnswerDto answerDto) {
        answerDto.setId(answer.getId());
        answerDto.setContent(answer.getContent());
        answerDto.setAuthor(answer.getAuthor().getUserName());
        answerDto.setCreationDate(answer.getCreationDate());
    }

    public static void populateEntity(Answer entity, AnswerDto answerDto, Question question, User author) {
        entity.setId(answerDto.getId());
        entity.setContent(answerDto.getContent());
        entity.setQuestion(question);
        entity.setAuthor(author);
    }
}
