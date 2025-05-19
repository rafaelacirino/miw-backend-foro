package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.QuestionService;
import es.upm.miw.foro.util.StatusMsg;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestConfig
class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private Authentication authentication;

    private static final Long QUESTION_ID = 1L;
    private static final String TITLE = "Question title";
    private static final String DESCRIPTION = "Question description";
    private static final LocalDate CREATION_DATE = LocalDate.now();
    private static final String USER_EMAIL = "user@email.com";
    private static final String UNEXPECTED_ERROR = "Unexpected error";


    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(USER_EMAIL);
    }

    @Test
    void testCreateQuestion() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setTitle(TITLE);
        dto.setDescription(DESCRIPTION);

        when(questionService.createQuestion(any(QuestionDto.class))).thenReturn(dto);

        // Act
        ResponseEntity<QuestionDto> response = questionController.createQuestion(dto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
        assertNotNull(response.getBody());
        verify(questionService, times(1)).createQuestion(dto);
    }

    @Test
    void testCreateQuestion_ServiceExceptionUnauthorized() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        when(questionService.createQuestion(any(QuestionDto.class)))
                                            .thenThrow(new ServiceException("Unauthorized: User not authenticated"));

        // Act
        ResponseEntity<QuestionDto> response = questionController.createQuestion(dto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).createQuestion(dto);
    }

    @Test
    void testCreateQuestion_ServiceExceptionOther() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        when(questionService.createQuestion(any(QuestionDto.class)))
                .thenThrow(new ServiceException("Some other error"));

        // Act
        ResponseEntity<QuestionDto> response = this.questionController.createQuestion(dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).createQuestion(dto);
    }

    @Test
    void testCreateQuestion_GenericException() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        when(questionService.createQuestion(any(QuestionDto.class)))
                .thenThrow(new RuntimeException(UNEXPECTED_ERROR));

        // Act
        ResponseEntity<QuestionDto> response = this.questionController.createQuestion(dto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).createQuestion(dto);
    }

    @Test
    void testGetQuestionById() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setId(QUESTION_ID);
        dto.setTitle(TITLE);

        when(questionService.getQuestionById(anyLong())).thenReturn(dto);

        // Act
        ResponseEntity<QuestionDto> response = questionController.getQuestionById(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        assertNotNull(response.getBody());
        verify(questionService, times(1)).getQuestionById(QUESTION_ID);
    }

    @Test
    void testGetQuestionById_NotFound() {
        // Arrange
        when(questionService.getQuestionById(anyLong())).thenThrow(new ServiceException("Question not found"));

        // Act
        ResponseEntity<QuestionDto> response = questionController.getQuestionById(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).getQuestionById(QUESTION_ID);
    }

    @Test
    void testGetQuestionById_NullDto() {
        // Arrange
        when(questionService.getQuestionById(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<QuestionDto> response = questionController.getQuestionById(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).getQuestionById(QUESTION_ID);
    }

    @Test
    void testGetQuestionById_InternalError() {
        // Arrange
        when(questionService.getQuestionById(anyLong())).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<QuestionDto> response = questionController.getQuestionById(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).getQuestionById(QUESTION_ID);
    }

    @Test
    void testGetQuestions() {
        // Arrange
        Page<QuestionDto> questionPage = new PageImpl<>(List.of(new QuestionDto()));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(questionService.getQuestions(null, pageable)).thenReturn(questionPage);

        // Act
        ResponseEntity<Page<QuestionDto>> response = questionController.getQuestions(null, 0, 10,
                                                                                "id", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        assertNotNull(response.getBody());
        verify(questionService, times(1)).getQuestions(null, pageable);
    }

    @Test
    void testGetQuestionsWithDescending() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setId(QUESTION_ID);
        dto.setTitle(TITLE);
        Page<QuestionDto> questionPage = new PageImpl<>(List.of(dto));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        when(questionService.getQuestions(null, pageable)).thenReturn(questionPage);

        // Act
        ResponseEntity<Page<QuestionDto>> response = questionController.getQuestions(null, 0, 10,
                                                                                        "id", "desc");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        assertNotNull(response.getBody());
        verify(questionService, times(1)).getQuestions(null, pageable);
    }

    @Test
    void testGetQuestionsWithFilters() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setId(QUESTION_ID);
        dto.setTitle(TITLE);
        Page<QuestionDto> questionPage = new PageImpl<>(List.of(dto));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(questionService.getQuestions(TITLE, pageable)).thenReturn(questionPage);

        // Act
        ResponseEntity<Page<QuestionDto>> response = questionController.getQuestions(TITLE, 0, 10,
                "id", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        assertNotNull(response.getBody());
        verify(questionService, times(1)).getQuestions(TITLE, pageable);
    }

    @Test
    void testGetUsersServiceException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(questionService.getQuestions(null, pageable))
                .thenThrow(new ServiceException("Unauthorized: Only admins can list users"));

        // Act
        ResponseEntity<Page<QuestionDto>> response = questionController.getQuestions(null,
                                                                        0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).getQuestions(null, pageable);
    }

    @Test
    void testGetUsersGenericException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(questionService.getQuestions(null, pageable))
                .thenThrow(new RuntimeException(UNEXPECTED_ERROR));

        // Act
        ResponseEntity<Page<QuestionDto>> response = questionController.getQuestions(null, 0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).getQuestions(null, pageable);
    }

    @Test
    void testUpdateQuestion() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setTitle(TITLE);

        when(questionService.updateQuestion(anyLong(), any(QuestionDto.class))).thenReturn(dto);

        // Act
        ResponseEntity<QuestionDto> response = this.questionController.updateQuestion(QUESTION_ID, dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(questionService, times(1)).updateQuestion(QUESTION_ID, dto);
    }

    @Test
    void testUpdateQuestionUnauthorized() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        when(questionService.updateQuestion(anyLong(), any(QuestionDto.class)))
                .thenThrow(new ServiceException(StatusMsg.UNAUTHORIZED));

        // Act
        ResponseEntity<QuestionDto> response = questionController.updateQuestion(QUESTION_ID, dto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateQuestionBadRequest() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        when(questionService.updateQuestion(anyLong(), any(QuestionDto.class)))
                .thenThrow(new ServiceException("Invalid data"));

        // Act
        ResponseEntity<QuestionDto> response = questionController.updateQuestion(QUESTION_ID, dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateQuestionInternalError() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        when(questionService.updateQuestion(anyLong(), any(QuestionDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<QuestionDto> response = questionController.updateQuestion(QUESTION_ID, dto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteQuestion() {
        // Act
        ResponseEntity<Void> response = this.questionController.deleteQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).deleteQuestion(QUESTION_ID);
    }

    @Test
    void testDeleteQuestionForbidden() {
        // Arrange
        doThrow(new ServiceException(StatusMsg.UNAUTHORIZED))
                .when(questionService).deleteQuestion(anyLong());

        // Act
        ResponseEntity<Void> response = questionController.deleteQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(questionService, times(1)).deleteQuestion(QUESTION_ID);
    }

    @Test
    void testDeleteQuestionNotFound() {
        // Arrange
        doThrow(new ServiceException("Question not found"))
                .when(questionService).deleteQuestion(anyLong());

        // Act
        ResponseEntity<Void> response = questionController.deleteQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(questionService, times(1)).deleteQuestion(QUESTION_ID);
    }

    @Test
    void testDeleteQuestionInternalError() {
        // Arrange
        doThrow(new RuntimeException("Database error"))
                .when(questionService).deleteQuestion(anyLong());

        // Act
        ResponseEntity<Void> response = questionController.deleteQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(questionService, times(1)).deleteQuestion(QUESTION_ID);
    }

    @Test
    void testGetMyQuestions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<QuestionDto> questions = Collections.singletonList(new QuestionDto());
        Page<QuestionDto> page = new PageImpl<>(questions);

        when(questionService.getMyQuestions(anyString(), anyString(), any(LocalDate.class), any(Pageable.class)))
                                                                                                    .thenReturn(page);

        // Act
        ResponseEntity<Page<QuestionDto>> response = this.questionController.getMyQuestions(
                authentication, TITLE, CREATION_DATE, 0, 10, "id", "asc"
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(page, response.getBody());
        verify(questionService, times(1)).getMyQuestions(USER_EMAIL, TITLE, CREATION_DATE, pageable);
    }

    @Test
    void testGetMyQuestionsInternalError() {
        // Arrange
        when(questionService.getMyQuestions(anyString(), anyString(), any(LocalDate.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Page<QuestionDto>> response = questionController.getMyQuestions(
                authentication, TITLE, CREATION_DATE, 0, 10, "id", "asc"
        );

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testRegisterView() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act
        ResponseEntity<Void> response = questionController.registerView(QUESTION_ID, request);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(questionService, times(1)).registerView(QUESTION_ID, request);
    }

    @Test
    void testRegisterViewVerifyServiceCall() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act
        questionController.registerView(QUESTION_ID, request);

        // Assert
        verify(questionService).registerView(QUESTION_ID, request);
    }
}
