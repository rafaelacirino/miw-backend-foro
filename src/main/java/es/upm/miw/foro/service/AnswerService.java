package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.AnswerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AnswerService {

    AnswerDto createAnswer(Long questionId, AnswerDto answerDto);

    List<AnswerDto> getAnswersByQuestionId(Long questionId);

    AnswerDto updateAnswer(Long questionId, AnswerDto answerDto);

    Page<AnswerDto> getMyAnswers(String author, String content, LocalDateTime creationDate, Pageable pageable);

    void deleteAnswer(Long id);
}
