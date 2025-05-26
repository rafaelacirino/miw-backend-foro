package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.AnswerService;
import es.upm.miw.foro.service.QuestionService;
import es.upm.miw.foro.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestConfig
class AnswerControllerTest {

    @Mock
    private AnswerService answerService;

    @InjectMocks
    private AnswerController answerController;

    private AnswerDto answerDto;
    private Authentication authentication;
    private static final Long QUESTION_ID = 1L;
    private static final Long ANSWER_ID = 1L;
    private static final String USER_EMAIL = "user@email.com";
    private static final String QUESTION_TITLE = "Question Title";
    private static final String CONTENT = "Content";
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();

    @Autowired
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        answerDto = new AnswerDto();
        answerDto.setId(ANSWER_ID);
        answerDto.setContent("Test answer content");
        answerDto.setQuestionId(QUESTION_ID);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(USER_EMAIL);
    }

    @Test
    void testCreateAnswer() {
        // Arrange
        when(answerService.createAnswer(anyLong(), any(AnswerDto.class))).thenReturn(answerDto);

        // Act
        ResponseEntity<AnswerDto> response = answerController.createAnswer(QUESTION_ID, answerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(answerDto, response.getBody());
        verify(answerService, times(1)).createAnswer(QUESTION_ID, answerDto);
    }

    @Test
    void testCreateAnswerUnauthorized() {
        // Arrange
        when(answerService.createAnswer(anyLong(), any(AnswerDto.class)))
                .thenThrow(new ServiceException(MessageUtil.UNAUTHORIZED));

        // Act
        ResponseEntity<AnswerDto> response = answerController.createAnswer(QUESTION_ID, answerDto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateAnswerBadRequest() {
        // Arrange
        when(answerService.createAnswer(anyLong(), any(AnswerDto.class)))
                .thenThrow(new ServiceException("Invalid data"));

        // Act
        ResponseEntity<AnswerDto> response = answerController.createAnswer(QUESTION_ID, answerDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateAnswerInternalServerError() {
        // Arrange
        when(answerService.createAnswer(anyLong(), any(AnswerDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<AnswerDto> response = answerController.createAnswer(QUESTION_ID, answerDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAnswersByQuestion() {
        // Arrange
        List<AnswerDto> answers = List.of(answerDto);
        when(answerService.getAnswersByQuestionId(anyLong())).thenReturn(answers);

        // Act
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(answerDto, response.getBody().get(0));
        verify(answerService, times(1)).getAnswersByQuestionId(QUESTION_ID);
    }

    @Test
    void testGetAnswersByQuestionBadRequest() {
        // Arrange
        when(answerService.getAnswersByQuestionId(anyLong()))
                .thenThrow(new ServiceException("Invalid question ID"));

        // Act
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAnswersByQuestionInternalServerError() {
        // Arrange
        when(answerService.getAnswersByQuestionId(anyLong()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAnswersByQuestionEmptyList() {
        // Arrange
        when(answerService.getAnswersByQuestionId(anyLong())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(QUESTION_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetMyAnswers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<AnswerDto> answers = Collections.singletonList(new AnswerDto());
        Page<AnswerDto> page = new PageImpl<>(answers);

        when(answerService.getMyAnswers(anyString(), anyString(), anyString(), any(LocalDateTime.class),
                                                                                any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<AnswerDto>> response = answerController.getMyAnswers(authentication, QUESTION_TITLE,
                                                CONTENT, DATE_TIME, 0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(page, response.getBody());
        verify(answerService, times(1)).getMyAnswers(USER_EMAIL, QUESTION_TITLE, CONTENT, DATE_TIME, pageable);
    }

    @Test
    void testGetMyAnswers_InternalServerError() {
        // Arrange
        when(answerService.getMyAnswers(anyString(), anyString(), anyString(), any(LocalDateTime.class),
                any(Pageable.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Page<AnswerDto>> response = answerController.getMyAnswers(authentication, QUESTION_TITLE,
                CONTENT, DATE_TIME, 0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateAnswer() {
        // Arrange
        AnswerDto dto = new AnswerDto();
        dto.setContent(CONTENT);

        when(answerService.updateAnswer(anyLong(), any(AnswerDto.class))).thenReturn(dto);

        // Act
        ResponseEntity<AnswerDto> response = answerController.updateAnswer(ANSWER_ID, dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(answerService, times(1)).updateAnswer(ANSWER_ID, dto);
    }

    @Test
    void testUpdateAnswer_Unauthorized() {
        // Arrange
        AnswerDto dto = new AnswerDto();
        when(answerService.updateAnswer(anyLong(), any(AnswerDto.class)))
                                .thenThrow(new ServiceException(MessageUtil.UNAUTHORIZED));

        // Act
        ResponseEntity<AnswerDto> response = answerController.updateAnswer(ANSWER_ID, dto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateAnswer_BadRequest() {
        // Arrange
        AnswerDto dto = new AnswerDto();
        when(answerService.updateAnswer(anyLong(), any(AnswerDto.class)))
                .thenThrow(new ServiceException("Invalid data"));

        // Act
        ResponseEntity<AnswerDto> response = answerController.updateAnswer(ANSWER_ID, dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateAnswer_InternalError() {
        // Arrange
        AnswerDto dto = new AnswerDto();
        when(answerService.updateAnswer(anyLong(), any(AnswerDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<AnswerDto> response = answerController.updateAnswer(ANSWER_ID, dto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteAnswer() {
        // Act
        ResponseEntity<Void> response = answerController.deleteAnswer(ANSWER_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(answerService, times(1)).deleteAnswer(ANSWER_ID);
    }

    @Test
    void testDeleteAnswer_Forbidden() {
        // Arrange
        doThrow(new ServiceException(MessageUtil.UNAUTHORIZED)).when(answerService).deleteAnswer(anyLong());

        // Act
        ResponseEntity<Void> response = answerController.deleteAnswer(ANSWER_ID);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(answerService, times(1)).deleteAnswer(ANSWER_ID);
    }

    @Test
    void testDeleteAnswer_NotFound() {
        // Arrange
        doThrow(new ServiceException("Answer not found")).when(answerService).deleteAnswer(anyLong());

        // Act
        ResponseEntity<Void> response = answerController.deleteAnswer(ANSWER_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(answerService, times(1)).deleteAnswer(ANSWER_ID);
    }

    @Test
    void testDeleteAnswer_InternalServerError() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(answerService).deleteAnswer(anyLong());

        // Act
        ResponseEntity<Void> response = answerController.deleteAnswer(ANSWER_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(answerService, times(1)).deleteAnswer(ANSWER_ID);
    }
}
