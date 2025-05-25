package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.Role;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.QuestionRepository;
import es.upm.miw.foro.service.impl.QuestionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestConfig
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserService userService;

    @Mock
    private Validator validator;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private User authenticatedUser;
    private Question question;
    private QuestionDto questionDto;
    private static final Long QUESTION_ID = 1L;
    private static final String TITLE = "Test Question";
    private static final String DESCRIPTION = "Test Description";
    private static final String EMAIL = "test@test.com";
    private static final String USERNAME = "test";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private static final int VIEWS = 0;

    @BeforeEach
    void setUp() {
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setUserName(USERNAME);
        authenticatedUser.setRole(Role.MEMBER);

        question = new Question();
        question.setId(QUESTION_ID);
        question.setTitle(TITLE);
        question.setDescription(DESCRIPTION);
        question.setAuthor(authenticatedUser);
        question.setCreationDate(CREATION_DATE);
        question.setViews(VIEWS);

        questionDto = new QuestionDto();
        questionDto.setId(QUESTION_ID);
        questionDto.setTitle(TITLE);
        questionDto.setDescription(DESCRIPTION);
        questionDto.setAuthor(USERNAME);
        questionDto.setCreationDate(CREATION_DATE);
        questionDto.setViews(VIEWS);
    }

    @Test
    void testCreateQuestion_success() {
        // Arrange
        when(validator.validate(any(QuestionDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        QuestionDto result = questionService.createQuestion(questionDto);

        // Assert
        assertNotNull(result);
        assertEquals(QUESTION_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(USERNAME, result.getAuthor());
        assertEquals(CREATION_DATE, result.getCreationDate());
        assertEquals(VIEWS, result.getViews());
        assertTrue(result.getAnswers() == null || result.getAnswers().isEmpty());

        //Verify
        verify(validator, times(1)).validate(any(QuestionDto.class));
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testCreateQuestion_validationFailurte() {
        // Arrange
        Set<ConstraintViolation<QuestionDto>> violations = new HashSet<>();
        ConstraintViolation<QuestionDto> violation = mock(ConstraintViolation.class);
        jakarta.validation.Path propertyPath = mock(jakarta.validation.Path.class);
        when(propertyPath.toString()).thenReturn("title");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("Title cannot be null");
        violations.add(violation);
        when(validator.validate(any(QuestionDto.class))).thenReturn(violations);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.createQuestion(questionDto));
        assertEquals("title: Title cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        // Verify
        verify(validator, times(1)).validate(any(QuestionDto.class));
        verify(userService, never()).getAuthenticatedUser();
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testCreateQuestion_dataAccessException() {
        // Arrange
        when(validator.validate(any(QuestionDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(questionRepository.save(any(Question.class))).thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> questionService.createQuestion(questionDto));
        assertEquals("Error while saving question", exception.getMessage());

        // Verify
        verify(validator, times(1)).validate(any(QuestionDto.class));
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testCreateQuestion_unexpectedException() {
        // Arrange
        when(validator.validate(any(QuestionDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.createQuestion(questionDto));
        assertEquals("Unexpected error while creating question", exception.getMessage());

        // Verify
        verify(validator, times(1)).validate(any(QuestionDto.class));
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testGetQuestionById_success() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        // Act
        QuestionDto result = questionService.getQuestionById(QUESTION_ID);

        // Assert
        assertNotNull(result);
        assertEquals(QUESTION_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(USERNAME, result.getAuthor());

        // Verify
        verify(questionRepository, times(1)).findById(QUESTION_ID);
    }

    @Test
    void testGetQuestionById_notFound() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.getQuestionById(QUESTION_ID));
        assertEquals("Question not found", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).findById(QUESTION_ID);
    }

    @Test
    void testGetQuestionById_dataAccessException() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> questionService.getQuestionById(QUESTION_ID));
        assertEquals("Error while retrieving question", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).findById(QUESTION_ID);
    }

    @Test
    void testGetQuestions_withTitle() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> questionPage = new PageImpl<>(Collections.singletonList(question));
        when(questionRepository.findByTitleContainingIgnoreCase(TITLE, pageable)).thenReturn(questionPage);

        // Act
        Page<QuestionDto> result = questionService.getQuestions(TITLE, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(QUESTION_ID, result.getContent().get(0).getId());
        assertEquals(TITLE, result.getContent().get(0).getTitle());

        // Verify
        verify(questionRepository, times(1)).findByTitleContainingIgnoreCase(TITLE, pageable);
        verify(questionRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void testGetQuestions_withoutTitle() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> questionPage = new PageImpl<>(Collections.singletonList(question));
        when(questionRepository.findAll(pageable)).thenReturn(questionPage);

        // Act
        Page<QuestionDto> result = questionService.getQuestions(null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(QUESTION_ID, result.getContent().get(0).getId());

        // Verify
        verify(questionRepository, times(1)).findAll(pageable);
        verify(questionRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void testGetQuestions_dataAccessException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(questionRepository.findAll(pageable)).thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> questionService.getQuestions(null, pageable));
        assertEquals("Error while getting questions", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).findAll(pageable);
    }

    @Test
    void testSearchQuestions_success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> questionPage = new PageImpl<>(Collections.singletonList(question));
        when(questionRepository.searchByTitleOrDescriptionContainingIgnoreCase(TITLE, pageable)).thenReturn(questionPage);

        // Act
        Page<QuestionDto> result = questionService.searchQuestions(TITLE, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(QUESTION_ID, result.getContent().get(0).getId());

        // Verify
        verify(questionRepository, times(1)).searchByTitleOrDescriptionContainingIgnoreCase(TITLE, pageable);
    }

    @Test
    void testSearchQuestions_dataAccessException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(questionRepository.searchByTitleOrDescriptionContainingIgnoreCase(TITLE, pageable))
                .thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> questionService.searchQuestions(TITLE, pageable));
        assertEquals("Error searching questions", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).searchByTitleOrDescriptionContainingIgnoreCase(TITLE, pageable);
    }

    @Test
    void testUpdateQuestion_success() {
        // Arrange
        when(validator.validate(any(QuestionDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        QuestionDto result = questionService.updateQuestion(QUESTION_ID, questionDto);

        // Assert
        assertNotNull(result);
        assertEquals(QUESTION_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(DESCRIPTION, result.getDescription());

        // Verify
        verify(validator, times(1)).validate(any(QuestionDto.class));
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).findById(QUESTION_ID);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testUpdateQuestion_notFound() {
        // Arrange
        when(validator.validate(any(QuestionDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.updateQuestion(QUESTION_ID, questionDto));
        assertEquals("Question not found with id: " + QUESTION_ID, exception.getMessage());

        // Verify
        verify(validator, times(1)).validate(any(QuestionDto.class));
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).findById(QUESTION_ID);
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testUpdateQuestion_unauthorized() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setEmail("other@example.com");
        differentUser.setRole(Role.MEMBER);

        when(validator.validate(any(QuestionDto.class))).thenReturn(Collections.emptySet());
        when(userService.getAuthenticatedUser()).thenReturn(differentUser);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.updateQuestion(QUESTION_ID, questionDto));
        assertEquals("You are not authorized to update this question", exception.getMessage());

        // Verify
        verify(validator, times(1)).validate(any(QuestionDto.class));
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).findById(QUESTION_ID);
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testDeleteQuestion_success() {
        // Arrange
        when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        doNothing().when(questionRepository).delete(question);

        // Act
        assertDoesNotThrow(() -> questionService.deleteQuestion(QUESTION_ID));

        // Verify
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).findById(QUESTION_ID);
        verify(questionRepository, times(1)).delete(question);
    }

    @Test
    void testDeleteQuestion_notFound() {
        // Arrange
        when(userService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.deleteQuestion(QUESTION_ID));
        assertEquals("Question not found with id: " + QUESTION_ID, exception.getMessage());

        // Verify
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).findById(QUESTION_ID);
        verify(questionRepository, never()).delete(any(Question.class));
    }

    @Test
    void testDeleteQuestion_unauthorized() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setEmail("other@example.com");
        differentUser.setRole(Role.MEMBER);

        when(userService.getAuthenticatedUser()).thenReturn(differentUser);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.deleteQuestion(QUESTION_ID));
        assertEquals("You are not authorized to delete this question", exception.getMessage());

        // Verify
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).findById(QUESTION_ID);
        verify(questionRepository, never()).delete(any(Question.class));
    }

    @Test
    void testDeleteQuestion_adminAuthorized() {
        // Arrange
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);

        when(userService.getAuthenticatedUser()).thenReturn(adminUser);
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        doNothing().when(questionRepository).delete(question);

        // Act
        assertDoesNotThrow(() -> questionService.deleteQuestion(QUESTION_ID));

        // Verify
        verify(userService, times(1)).getAuthenticatedUser();
        verify(questionRepository, times(1)).findById(QUESTION_ID);
        verify(questionRepository, times(1)).delete(question);
    }

    @Test
    void testIsQuestionAuthor_success() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        // Act
        boolean result = questionService.isQuestionAuthor(QUESTION_ID, EMAIL);

        // Assert
        assertTrue(result);

        // Verify
        verify(questionRepository, times(1)).findById(QUESTION_ID);
    }

    @Test
    void testIsQuestionAuthor_notAuthor() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        // Act
        boolean result = questionService.isQuestionAuthor(QUESTION_ID, "other@example.com");

        // Assert
        assertFalse(result);

        // Verify
        verify(questionRepository, times(1)).findById(QUESTION_ID);
    }

    @Test
    void testIsQuestionAuthor_notFound() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.isQuestionAuthor(QUESTION_ID, EMAIL));
        assertEquals("Question not found with id: " + QUESTION_ID, exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).findById(QUESTION_ID);
    }

    @Test
    void testGetMyQuestions_success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime fromDate = LocalDateTime.now().minusDays(10);
        Page<Question> questionPage = new PageImpl<>(Collections.singletonList(question));
        when(questionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(questionPage);

        // Act
        Page<QuestionDto> result = questionService.getMyQuestions(EMAIL, TITLE, fromDate, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(QUESTION_ID, result.getContent().get(0).getId());

        // Verify
        verify(questionRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetMyQuestions_unexpectedException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime fromDate = LocalDateTime.now().minusDays(10);
        when(questionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> questionService.getMyQuestions(EMAIL, TITLE, fromDate, pageable));
        assertEquals("Error retrieving user questions with filters", exception.getMessage());

        // Verify
        verify(questionRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testRegisterView_incrementsViewForNewSession() {
        // Arrange
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = mock(HttpSession.class);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getId()).thenReturn("session-abc");

        // Act
        questionService.registerView(QUESTION_ID, mockRequest);

        // Assert
        assertEquals(1, question.getViews());
        assertTrue(question.getViewedBySessions().contains("session-abc"));
    }
}
