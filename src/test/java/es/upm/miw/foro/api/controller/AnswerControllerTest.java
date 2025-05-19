package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.AnswerService;
import es.upm.miw.foro.util.StatusMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

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
    private final Long questionId = 1L;
    private static final String USER_EMAIL = "user@email.com";

    @BeforeEach
    void setUp() {
        answerDto = new AnswerDto();
        answerDto.setId(1L);
        answerDto.setContent("Test answer content");
        answerDto.setQuestionId(questionId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(USER_EMAIL);
    }

    @Test
    void testCreateAnswer() {
        // Arrange
        when(answerService.createAnswer(anyLong(), any(AnswerDto.class))).thenReturn(answerDto);

        // Act
        ResponseEntity<AnswerDto> response = answerController.createAnswer(questionId, answerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(answerDto, response.getBody());
        verify(answerService, times(1)).createAnswer(questionId, answerDto);
    }

    @Test
    void testCreateAnswerUnauthorized() {
        // Arrange
        when(answerService.createAnswer(anyLong(), any(AnswerDto.class)))
                .thenThrow(new ServiceException(StatusMsg.UNAUTHORIZED));

        // Act
        ResponseEntity<AnswerDto> response = answerController.createAnswer(questionId, answerDto);

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
        ResponseEntity<AnswerDto> response = answerController.createAnswer(questionId, answerDto);

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
        ResponseEntity<AnswerDto> response = answerController.createAnswer(questionId, answerDto);

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
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(questionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(answerDto, response.getBody().getFirst());
        verify(answerService, times(1)).getAnswersByQuestionId(questionId);
    }

    @Test
    void testGetAnswersByQuestionBadRequest() {
        // Arrange
        when(answerService.getAnswersByQuestionId(anyLong()))
                .thenThrow(new ServiceException("Invalid question ID"));

        // Act
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(questionId);

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
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(questionId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAnswersByQuestionEmptyList() {
        // Arrange
        when(answerService.getAnswersByQuestionId(anyLong())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AnswerDto>> response = answerController.getAnswersByQuestion(questionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
