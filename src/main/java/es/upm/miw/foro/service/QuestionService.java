package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.QuestionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    QuestionDto createQuestion(QuestionDto questionDto);

    QuestionDto getQuestionById(Long id);

    List<QuestionDto> getQuestionByTitle(String title);

    Page<QuestionDto> getAllQuestions(String title, Pageable pageable);

    QuestionDto updateQuestion(Long id, QuestionDto questionDto);

    boolean isQuestionAuthor(Long questionId, String username);

    void deleteQuestion(Long id);
}
