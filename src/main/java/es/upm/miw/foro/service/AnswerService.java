package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.AnswerDto;

import java.util.List;

public interface AnswerService {

    AnswerDto createAnswer(Long questionId, AnswerDto answerDto);

    List<AnswerDto> getAnswersByQuestionId(Long questionId);
}
