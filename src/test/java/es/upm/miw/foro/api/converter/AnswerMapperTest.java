package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class AnswerMapperTest {

    private static final Long ANSWER_ID = 1L;
    private static final String CONTENT = "Answer Name";
    private static final String AUTHOR = "author";
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2020, 1, 1, 1, 1);

    @Test
    void toAnswerDto_thenMapsToDto() {
        // Arrange
        Answer answer = createAnswerEntity();

        // Act
        AnswerDto answerDto = AnswerMapper.toAnswerDto(answer);

        // Assert
        assertNotNull(answerDto);
        assertEquals(ANSWER_ID, answerDto.getId());
        assertEquals(CONTENT, answerDto.getContent());
        assertEquals(AUTHOR, answerDto.getAuthor());
        assertEquals(DATE_TIME, answerDto.getCreationDate());
    }

    @Test
    void toAnswerDtoWithNullAnswer_thenReturnsNull() {
        // Act
        AnswerDto answerDto = AnswerMapper.toAnswerDto(null);

        // Assert
        assertNull(answerDto);
    }

    @Test
    void toEntity_thenMapsToEntity() {
        // Arrange
        AnswerDto answerDto = createAnswerDto();
        Question question = createQuestionEntity();
        User author = createUserEntity();

        // Act
        Answer answer = AnswerMapper.toEntity(answerDto, question, author);

        // Assert
        assertNotNull(answer);
        assertEquals(ANSWER_ID, answer.getId());
        assertEquals(CONTENT, answer.getContent());
        assertEquals(question, answer.getQuestion());
        assertEquals(AUTHOR, answer.getAuthor().getUserName());
        assertNull(answer.getCreationDate());
    }

    @Test
    void toEntityWithNullDto_thenReturnsNull() {
        // Act
        Answer answer = AnswerMapper.toEntity(null, createQuestionEntity(), createUserEntity());

        // Assert
        assertNull(answer);
    }

    @Test
    void toDtoList_thenMapsToDtoList() {
        // Arrange
        List<Answer> answers = Arrays.asList(createAnswerEntity(), createAnswerEntity());

        // Act
        List<AnswerDto> answerDtos = AnswerMapper.toDtoList(answers);

        // Assert
        assertNotNull(answerDtos);
        assertEquals(2, answerDtos.size());
        assertEquals(ANSWER_ID, answerDtos.getFirst().getId());
        assertEquals(CONTENT, answerDtos.getFirst().getContent());
        assertEquals(AUTHOR, answerDtos.getFirst().getAuthor());
        assertEquals(DATE_TIME, answerDtos.getFirst().getCreationDate());
    }

    @Test
    void toDtoListWithNullList_thenReturnsEmptyList() {
        // Act
        List<AnswerDto> answerDtos = AnswerMapper.toDtoList(null);

        // Assert
        assertNotNull(answerDtos);
        assertTrue(answerDtos.isEmpty());
    }

    private Answer createAnswerEntity() {
        Answer answer = new Answer();
        answer.setId(ANSWER_ID);
        answer.setContent(CONTENT);
        answer.setQuestion(createQuestionEntity());
        answer.setAuthor(createUserEntity());
        answer.setCreationDate(DATE_TIME);

        return answer;
    }

    private AnswerDto createAnswerDto() {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setId(ANSWER_ID);
        answerDto.setContent(CONTENT);
        answerDto.setAuthor(AUTHOR);
        answerDto.setCreationDate(DATE_TIME);
        return answerDto;
    }

    private Question createQuestionEntity() {
        Question question = new Question();
        question.setId(2L);
        question.setTitle("Question Title");

        return question;
    }

    private User createUserEntity() {
        User user = new User();
        user.setId(3L);
        user.setUserName(AUTHOR);

        return user;
    }
}
