package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerMapper {

    private AnswerMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static AnswerDto toAnswerDto(Answer answer) {
        if (answer == null) {
            return null;
        }
        AnswerDto answerDto = new AnswerDto();
        populateDto(answer, answerDto);
        return answerDto;
    }

    public static Answer toEntity(AnswerDto answerDto, Question question) {
        if (answerDto == null) {
            return null;
        }
        Answer answer = new Answer();
        populateEntity(answer, answerDto, question);
        return answer;
    }

    public static List<AnswerDto> toDtoList(List<Answer> answers) {
        if (answers == null) {
            return Collections.emptyList();
        }
        return answers.stream()
                .map(AnswerMapper::toAnswerDto)
                .collect(Collectors.toList());
    }

    public static List<Answer> toEntityList(List<AnswerDto> answerDtos, Question question) {
        return answerDtos.stream()
                .map(dto -> toEntity(dto, question))
                .collect(Collectors.toList());
    }

    public static void populateDto(Answer answer, AnswerDto answerDto) {
        answerDto.setId(answer.getId());
        answerDto.setContent(answer.getContent());
        answerDto.setAuthor(answer.getAuthor().getUserName());
        answerDto.setCreationDate(answer.getCreationDate());
    }

    public static void populateEntity(Answer entity, AnswerDto answerDto, Question question) {
        entity.setId(answerDto.getId());
        entity.setContent(answerDto.getContent());
        entity.setCreationDate(answerDto.getCreationDate());
        entity.setQuestion(question);
    }
}
