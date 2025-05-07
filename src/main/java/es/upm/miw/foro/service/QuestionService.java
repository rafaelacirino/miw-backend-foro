package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.QuestionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface QuestionService {

    QuestionDto createQuestion(QuestionDto questionDto);

    QuestionDto getQuestionById(Long id);

    Page<QuestionDto> getQuestions(String title, Pageable pageable);

    Page<QuestionDto> searchQuestions(String query, Pageable pageable);

    QuestionDto updateQuestion(Long id, QuestionDto questionDto);

    void deleteQuestion(Long id);

    boolean isQuestionAuthor(Long questionId, String username);

    Page<QuestionDto> getMyQuestions(String email, String title, LocalDate fromDate, Pageable pageable);

    void incrementViews(Long id);
}
