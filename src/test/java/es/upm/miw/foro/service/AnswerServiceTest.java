package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.AnswerRepository;
import es.upm.miw.foro.persistence.repository.QuestionRepository;
import es.upm.miw.foro.service.impl.AnswerServiceImpl;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestConfig
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Validator validator;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private User author;
    private User questionAuthor;
    private Question question;
    private Answer answer;
    private AnswerDto answerDto;
    private static final Long QUESTION_ID = 1L;
    private static final Long ANSWER_ID = 2L;
    private static final Long AUTHOR_ID = 3L;
    private static final Long QUESTION_AUTHOR_ID = 4L;
    private static final String CONTENT = "Test answer content";
    private static final String AUTHOR_NAME = "testuser";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(AUTHOR_ID);
        author.setUserName(AUTHOR_NAME);

        questionAuthor = new User();
        questionAuthor.setId(QUESTION_AUTHOR_ID);
        questionAuthor.setUserName("questionAuthor");

        question = new Question();
        question.setId(QUESTION_ID);
        question.setAuthor(questionAuthor);
        question.setTitle("Test Question");

        answer = new Answer();
        answer.setId(ANSWER_ID);
        answer.setContent(CONTENT);
        answer.setAuthor(author);
        answer.setQuestion(question);
        answer.setCreationDate(CREATION_DATE);

        answerDto = new AnswerDto();
        answerDto.setId(ANSWER_ID);
        answerDto.setContent(CONTENT);
        answerDto.setAuthor(AUTHOR_NAME);
        answerDto.setCreationDate(CREATION_DATE);
        answerDto.setQuestionId(QUESTION_ID);

        when(validator.validate(any(AnswerDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(author);
    }

    @Test
    void testCreateAnswer() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Act
        AnswerDto result = answerService.createAnswer(QUESTION_ID, answerDto);

        // Assert
        assertNotNull(result);
        assertEquals(ANSWER_ID, result.getId());
        assertEquals(CONTENT, result.getContent());
        assertEquals(AUTHOR_NAME, result.getAuthor());
        assertEquals(CREATION_DATE, result.getCreationDate());

        verify(validator).validate(answerDto);
        verify(questionRepository).findById(QUESTION_ID);
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void testCreateAnswerNotifyNewAnswer() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Act
        answerService.createAnswer(QUESTION_ID, answerDto);

        // Assert
        verify(notificationService).notifyNewAnswer(questionAuthor, question, answer);
    }

    @Test
    void testCreateAnswerNotifyNewAnswerSameAuthor() {
        // Arrange
        question.setAuthor(author);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Act
        answerService.createAnswer(QUESTION_ID, answerDto);

        // Assert
        verify(notificationService, never()).notifyNewAnswer(any(), any(), any());
    }

    @Test
    void testCreateAnswerQuestionNotFound() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ServiceException.class, () -> {
            answerService.createAnswer(QUESTION_ID, answerDto);
        });
        verify(answerRepository, never()).save(any());
    }

    @Test
    void testCreateAnswerRepositoryException() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        assertThrows(RepositoryException.class, () -> {
            answerService.createAnswer(QUESTION_ID, answerDto);
        });
    }

    @Test
    void testCreateAnswerServiceException() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThrows(ServiceException.class, () -> {
            answerService.createAnswer(QUESTION_ID, answerDto);
        });
    }

    @Test
    void testGetAnswersByQuestionId() {
        // Arrange
        List<Answer> answers = List.of(answer);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.findByQuestionOrderByCreationDateAsc(question)).thenReturn(answers);

        // Act
        List<AnswerDto> result = answerService.getAnswersByQuestionId(QUESTION_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ANSWER_ID, result.getFirst().getId());
        verify(questionRepository).findById(QUESTION_ID);
        verify(answerRepository).findByQuestionOrderByCreationDateAsc(question);
    }

    @Test
    void testGetAnswersByQuestionIdRepositoryException() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.findByQuestionOrderByCreationDateAsc(question))
                .thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        assertThrows(RepositoryException.class, () -> {
            answerService.getAnswersByQuestionId(QUESTION_ID);
        });
    }

    @Test
    void testGetAnswersByQuestionIdServiceException() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.findByQuestionOrderByCreationDateAsc(question))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        assertThrows(ServiceException.class, () -> {
            answerService.getAnswersByQuestionId(QUESTION_ID);
        });
    }

    @Test
    void testValidateAnswerDto() {
        // Arrange
        when(validator.validate(answerDto)).thenReturn(Collections.emptySet());
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Act (no exception expected)
        AnswerDto result = answerService.createAnswer(QUESTION_ID, answerDto);

        // Assert
        verify(validator).validate(answerDto);
        assertNotNull(result);
    }
}
