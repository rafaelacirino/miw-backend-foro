package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.Role;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.AnswerRepository;
import es.upm.miw.foro.persistence.repository.QuestionRepository;
import es.upm.miw.foro.service.impl.AnswerServiceImpl;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private static final String USER_EMAIL = "test@test.com";
    private static final String QUESTION_TITLE = "Test Question";
    private static final String CONTENT = "Test answer content";
    private static final String AUTHOR_NAME = "testuser";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(AUTHOR_ID);
        author.setUserName(AUTHOR_NAME);
        author.setEmail(USER_EMAIL);

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

    @Test
    void testUpdateAnswer() {
        // Arrange
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));
        when(validator.validate(any(AnswerDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(author);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Acct
        AnswerDto result = answerService.updateAnswer(ANSWER_ID, answerDto);

        // Assert
        assertNotNull(result);
        assertEquals(ANSWER_ID, result.getId());
        assertEquals(CONTENT, result.getContent());
        assertEquals(AUTHOR_NAME, result.getAuthor());
        assertEquals(CREATION_DATE, result.getCreationDate());

        // Verify
        verify(answerRepository, times(2)).findByIdWithAuthor(ANSWER_ID);
        verify(validator, times(1)).validate(any(AnswerDto.class));
        verify(userService, times(1)).getAuthenticatedUser();
        verify(answerRepository, times(1)).save(any(Answer.class));
        verify(answerRepository, times(2)).findByIdWithAuthor(ANSWER_ID);
    }

    @Test
    void testUpdateAnswer_answerNotFound_throwsException() {
        // Arrange
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ServiceException.class, () -> answerService.updateAnswer(ANSWER_ID, answerDto));
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
    }

    @Test
    void testUpdateAnswer_unauthorizedUser_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(9L);
        otherUser.setUserName("user");

        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        // Act & Assert
        assertThrows(ServiceException.class, () -> answerService.updateAnswer(ANSWER_ID, answerDto));
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
        verify(userService, times(1)).getAuthenticatedUser();
    }

    @Test
    void testUpdateAnswer_questionNotFound_throwsException() {
        // Arrange
        when(answerRepository.findById(ANSWER_ID)).thenReturn(Optional.of(answer));
        when(validator.validate(any(AnswerDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(author);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ServiceException.class, () -> answerService.updateAnswer(ANSWER_ID, answerDto));
    }

    @Test
    void testUpdateAnswer_repositorySaveThrowsException() {
        // Arrange
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));
        when(validator.validate(any(AnswerDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(author);
        when(questionRepository.findByIdWithAuthor(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenThrow(new DataAccessException("DB Error") {});

        // Act & Assert
        assertThrows(RepositoryException.class, () -> answerService.updateAnswer(ANSWER_ID, answerDto));
        verify(answerRepository, times(1)).save(any(Answer.class));
    }

    @Test
    void testGetMyAnswers_successfullyWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Answer> answers = Collections.singletonList(answer);
        Page<Answer> answerPage = new PageImpl<>(answers);

        when(answerRepository.findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable))).thenReturn(answerPage);

        Page<AnswerDto> result = answerService.getMyAnswers(USER_EMAIL, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(answerDto.getContent(), result.getContent().getFirst().getContent());

        verify(answerRepository, times(1)).findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable));
    }

    @Test
    void testGetMyAnswers_withAllFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Answer> answers = Collections.singletonList(answer);
        Page<Answer> answerPage = new PageImpl<>(answers);

        when(answerRepository.findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable))).thenReturn(answerPage);

        Page<AnswerDto> result = answerService.getMyAnswers(
                USER_EMAIL, QUESTION_TITLE, CONTENT, CREATION_DATE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(answerRepository, times(1)).findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable));
    }

    @Test
    void testGetMyAnswers_noResultsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Answer> emptyPage = new PageImpl<>(Collections.emptyList());

        when(answerRepository.findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable))).thenReturn(emptyPage);

        Page<AnswerDto> result = answerService.getMyAnswers(USER_EMAIL, null, null, null, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(answerRepository, times(1)).findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable));
    }

    @Test
    void testGetMyAnswers_repositoryThrowsDataAccessException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(answerRepository.findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable)))
                .thenThrow(new RuntimeException("DB error"));

        ServiceException exception = assertThrows(ServiceException.class, () ->
                answerService.getMyAnswers(USER_EMAIL, null, null, null, pageable));

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Error retrieving user answers with filters"));
        verify(answerRepository, times(1)).findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable));
    }

    @Test
    void testGetMyAnswers_customPageable() {
        Pageable pageable = PageRequest.of(1, 5, Sort.by("creationDate").descending());
        List<Answer> answers = Collections.singletonList(answer);
        int totalSize = 6;
        Page<Answer> answerPage = new PageImpl<>(answers, pageable, totalSize);

        when(answerRepository.findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable))).thenReturn(answerPage);

        Page<AnswerDto> result = answerService.getMyAnswers(USER_EMAIL, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());

        verify(answerRepository, times(1)).findAll(ArgumentMatchers.<Specification<Answer>>any(), eq(pageable));
    }

    @Test
    void testDeleteAnswer() {
        when(userService.getAuthenticatedUser()).thenReturn(author);
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));

        answerService.deleteAnswer(ANSWER_ID);

        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
        verify(answerRepository, times(1)).delete(answer);
    }

    @Test
    void testDeleteAnswer_answerNotFound_throwsException() {
        when(userService.getAuthenticatedUser()).thenReturn(author);
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () ->
                answerService.deleteAnswer(ANSWER_ID));

        assertTrue(exception.getMessage().contains("Answer not found"));
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
        verify(answerRepository, never()).delete(answer);
    }

    @Test
    void testDeleteAnswer_unauthorizedUser_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(999L);
        otherUser.setRole(Role.MEMBER);

        when(userService.getAuthenticatedUser()).thenReturn(otherUser);
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));

        ServiceException exception = assertThrows(ServiceException.class, () ->
                answerService.deleteAnswer(ANSWER_ID));

        assertEquals("You are not authorized to delete this answer", exception.getMessage());
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
        verify(answerRepository, never()).delete(answer);
    }

    @Test
    void testDeleteAnswer_repositoryDeleteThrowsDataAccessException() {
        when(userService.getAuthenticatedUser()).thenReturn(author);
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));
        doThrow(new DataAccessException("DB Error") {}).when(answerRepository).delete(answer);

        RepositoryException exception = assertThrows(RepositoryException.class, () ->
                answerService.deleteAnswer(ANSWER_ID));

        assertTrue(exception.getMessage().contains("Error while deleting answer"));
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
        verify(answerRepository, times(1)).delete(answer);
    }

    @Test
    void testDeleteAnswer_unexpectedException_throwsServiceException() {
        when(userService.getAuthenticatedUser()).thenThrow(new NullPointerException("Unexpected null"));

        ServiceException exception = assertThrows(ServiceException.class, () ->
                answerService.deleteAnswer(ANSWER_ID));

        assertTrue(exception.getMessage().contains("Unexpected error while deleting answer"));
        verify(answerRepository, never()).findById(any());
        verify(answerRepository, never()).delete(answer);
    }

    @Test
    void testIsAnswerAuthor_usernameMatches_returnTrue() {
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));

        boolean result = answerService.isAnswerAuthor(ANSWER_ID, USER_EMAIL);

        assertTrue(result);
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
    }

    @Test
    void testIsAnswerAuthor_usernameDoesNotMatch_returnFalse() {
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.of(answer));

        boolean result = answerService.isAnswerAuthor(ANSWER_ID, "Name");

        assertFalse(result);
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
    }

    @Test
    void testIsAnswerAuthor_answerNotFound_throwsException() {
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () ->
                answerService.isAnswerAuthor(ANSWER_ID, USER_EMAIL));

        assertTrue(exception.getMessage().contains("Answer not found"));
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
    }

    @Test
    void testIsAnswerAuthor_repositoryThrowsDataAccessException() {
        when(answerRepository.findByIdWithAuthor(ANSWER_ID)).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                answerService.isAnswerAuthor(ANSWER_ID, USER_EMAIL));

        assertTrue(exception.getMessage().contains("DB error"));
        verify(answerRepository, times(1)).findByIdWithAuthor(ANSWER_ID);
    }
}
