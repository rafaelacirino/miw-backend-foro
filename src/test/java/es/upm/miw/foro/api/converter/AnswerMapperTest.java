package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnswerMapperTest {

    private static final Long ANSWER_ID = 1L;
    private static final String CONTENT = "Answer Name";
    private static final String QUESTION = "Answer Question";
    private static final String AUTHOR = "author";
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2020, 1, 1, 1, 1);

    @Test
    void testToConstructorThrowsUnsupportedOperationException() {
         assertThrows(UnsupportedOperationException.class, AnswerMapper::new);
    }

    @Test
    void whenToAnswerDtoIsCalledWithValidAnswer_thenMapsToDto() {
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
    void whenToAnswerDtoIsCalledWithNullAnswer_thenReturnsNull() {
        // Act
        AnswerDto answerDto = AnswerMapper.toAnswerDto(null);

        // Assert
        assertNull(answerDto);
    }

    @Test
    void whenToEntityIsCalledWithValidDto_thenMapsToEntity() {
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
    void whenToEntityIsCalledWithNullDto_thenReturnsNull() {
        // Act
        Answer answer = AnswerMapper.toEntity(null, createQuestionEntity(), createUserEntity());

        // Assert
        assertNull(answer);
    }

    @Test
    void whenToDtoListIsCalledWithValidAnswers_thenMapsToDtoList() {
        // Arrange
        List<Answer> answers = Arrays.asList(createAnswerEntity(), createAnswerEntity());

        // Act
        List<AnswerDto> answerDtos = AnswerMapper.toDtoList(answers);

        // Assert
        assertNotNull(answerDtos);
        assertEquals(2, answerDtos.size());
        assertEquals(ANSWER_ID, answerDtos.get(0).getId());
        assertEquals(CONTENT, answerDtos.get(0).getContent());
        assertEquals(AUTHOR, answerDtos.get(0).getAuthor());
        assertEquals(DATE_TIME, answerDtos.get(0).getCreationDate());
    }

    @Test
    void whenToDtoListIsCalledWithNullList_thenReturnsEmptyList() {
        // Act
        List<AnswerDto> answerDtos = AnswerMapper.toDtoList(null);

        // Assert
        assertNotNull(answerDtos);
        assertTrue(answerDtos.isEmpty());
    }

    @Test
    void whenEntityListIsCalledWithValidDtos_thenMapsToEntityList() {
        // Arrange
        List<AnswerDto> answerDtos = Arrays.asList(createAnswerDto(), createAnswerDto());
        Question question = createQuestionEntity();
        User author = createUserEntity();

        // Act
        List<Answer> answers = AnswerMapper.toEntityList(answerDtos, question, author);

        // Assert
        assertNotNull(answers);
        assertEquals(2, answers.size());
        assertEquals(ANSWER_ID, answers.get(0).getId());
        assertEquals(CONTENT, answers.get(0).getContent());
        assertEquals(question, answers.get(0).getQuestion());
        assertEquals(author, answers.get(0).getAuthor());
    }

    @Test
    void whenToEntityListIsCalledWithEmptyList_thenReturnsEmptyList() {
        // Act
        List<Answer> answers = AnswerMapper.toEntityList(Collections.emptyList(), createQuestionEntity(), createUserEntity());

        // Assert
        assertNotNull(answers);
        assertTrue(answers.isEmpty());
    }

    @Test
    void whenPopulateDtoIsCalledWithValidAnswer_thenPopulatesDto() {
        // Arrange
        Answer answer = createAnswerEntity();
        AnswerDto answerDto = new AnswerDto();

        // Act
        AnswerMapper.populateDto(answer, answerDto);

        // Assert
        assertEquals(ANSWER_ID, answerDto.getId());
        assertEquals(CONTENT, answerDto.getContent());
        assertEquals(AUTHOR, answerDto.getAuthor());
        assertEquals(DATE_TIME, answerDto.getCreationDate());
    }

    @Test
    void whenPopulateEntityIsCalledWithValidDto_thenPopulatesEntity() {
        // Arrange
        AnswerDto answerDto = createAnswerDto();
        Answer answer = new Answer();
        Question question = createQuestionEntity();
        User author = createUserEntity();

        // Act
        AnswerMapper.populateEntity(answer, answerDto, question, author);

        // Assert
        assertEquals(ANSWER_ID, answer.getId());
        assertEquals(CONTENT, answer.getContent());
        assertEquals(question, answer.getQuestion());
        assertEquals(author, answer.getAuthor());
        assertNull(answer.getCreationDate());
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
