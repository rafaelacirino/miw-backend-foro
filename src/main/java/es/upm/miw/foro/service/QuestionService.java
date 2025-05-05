package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.QuestionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface QuestionService {

    QuestionDto createQuestion(QuestionDto questionDto);

    QuestionDto getQuestionById(UUID id);

    Page<QuestionDto> getQuestions(String title, Pageable pageable);

    Page<QuestionDto> searchQuestions(String query, Pageable pageable);

    QuestionDto updateQuestion(UUID id, QuestionDto questionDto);

    void deleteQuestion(UUID id);

    boolean isQuestionAuthor(UUID questionId, String username);

    Page<QuestionDto> getMyQuestions(String email, String title, LocalDate fromDate, Pageable pageable);

    void incrementViews(UUID id);
}
