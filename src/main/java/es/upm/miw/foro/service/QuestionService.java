package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.persistence.model.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Set;

public interface QuestionService {

    QuestionDto createQuestion(QuestionDto questionDto);

    QuestionDto getQuestionById(Long id);

    Page<QuestionDto> getQuestions(String title, boolean unanswered, Pageable pageable);

    Page<QuestionDto> searchQuestions(String query, Pageable pageable);

    QuestionDto updateQuestion(Long id, QuestionDto questionDto);

    void deleteQuestion(Long id);

    boolean isQuestionAuthor(Long questionId, String username);

    Page<QuestionDto> getMyQuestions(String email, String title, LocalDateTime fromDate, Pageable pageable);

    void registerView(Long questionId, HttpServletRequest request);

    Set<Tag> processTags(Set<String> tagNames);

}
