package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionMapperTest {

    private static final Long ID = 1L;
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final LocalDateTime DATE = LocalDateTime.now();
    private static final Integer VIEWS = 1;

    @Test
    void testToConstructorThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, QuestionMapper::new);
    }

    @Test
    void toQuestionDto_shouldMapEntityToDto() {
        // Arrange
        Question question = createQuestionEntityWithAnswers();

        // Act
        QuestionDto questionDto = QuestionMapper.toQuestionDto(question);

        // Assert
        assertNotNull(questionDto);
        assertEquals(question.getId(), questionDto.getId());
        assertEquals(question.getAuthor().getUserName(), questionDto.getAuthor());
        assertEquals(question.getTitle(), questionDto.getTitle());
        assertEquals(question.getDescription(), questionDto.getDescription());
        assertEquals(question.getCreationDate(), questionDto.getCreationDate());
        assertEquals(1, questionDto.getAnswers().size());
        assertEquals(question.getViews(), questionDto.getViews());
    }

    @Test
    void toQuestionDto_withValidEntity_returnsPopulatedDto() {
        // Arrange
        Question question = new Question();
        question.setId(1L);
        question.setTitle("Test");
        question.setDescription("Description");

        // Act
        QuestionDto dto = QuestionMapper.toQuestionDto(question);

        // Assert
        assertNotNull(dto);
        assertEquals("Test", dto.getTitle());
    }

   @Test
    void testEntity_shouldMapDtoToEntity() {
        // Arrange
        QuestionDto questionDto = createQuestionDto();
       User author = createUser();

       // Act
        Question question = QuestionMapper.toEntity(questionDto, author);

        // Assert
        assertNotNull(question);
        assertEquals(questionDto.getId(), question.getId());
        assertEquals(questionDto.getAuthor(), question.getAuthor().getUserName());
        assertEquals(questionDto.getTitle(), question.getTitle());
        assertEquals(questionDto.getDescription(), question.getDescription());
        assertEquals(questionDto.getCreationDate(), question.getCreationDate());
        assertEquals(questionDto.getViews(), question.getViews());
    }

    @Test
    void toDtoList_withNullList_shouldReturnEmptyList() {
        // Act
        List<QuestionDto> result = QuestionMapper.toDtoList(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntityList_withNullList_shouldReturnEmptyList() {
        // Act
        List<Question> result = QuestionMapper.toEntityList(null, createUser());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDtoList_shouldMapEntityListToDtoList() {
        // Arrange
        List<Question> questionList = Arrays.asList(createQuestionEntity(), createQuestionEntity());

        // Act
        List<QuestionDto> questionDtoList = QuestionMapper.toDtoList(questionList);

        // Assert
        assertNotNull(questionDtoList);
        assertEquals(questionList.size(), questionDtoList.size());
    }

    @Test
    void toEntityList_shouldMapDtoListToEntityList() {
        // Arrange
        List<QuestionDto> dtoList = Arrays.asList(createQuestionDto(), createQuestionDto());
        User author = createUser();

        // Act
        List<Question> entityList = QuestionMapper.toEntityList(dtoList, author);

        // Assert
        assertNotNull(entityList);
        assertEquals(dtoList.size(), entityList.size());
    }

    @Test
    void toDto_shouldReturnNullWhenEntityIsNull() {
        // Act
        QuestionDto dto = QuestionMapper.toQuestionDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void toQuestionDto_whenAnswersAreNull_shouldReturnDtoWithNullAnswers() {
        // Arrange
        Question question = createQuestionEntity();
        question.setAnswers(null);

        // Act
        QuestionDto dto = QuestionMapper.toQuestionDto(question);

        // Assert
        assertNull(dto.getAnswers(), "Answers should be null in DTO if not present in entity");
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        // Act
        Question question = QuestionMapper.toEntity(null, null);

        // Assert
        assertNull(question);
    }

    private Question createQuestionEntityWithAnswers() {
        Question question = new Question();
        question.setId(ID);
        question.setAuthor(createUser());
        question.setTitle(TITLE);
        question.setDescription(DESCRIPTION);
        question.setCreationDate(DATE);

        Answer answer = createAnswerEntity();
        answer.setAuthor(createUser());
        question.addAnswer(answer);
        question.setViews(VIEWS);

        return question;
    }

    private Question createQuestionEntity() {
        Question question = new Question();
        question.setId(ID);
        question.setAuthor(createUser());
        question.setTitle(TITLE);
        question.setDescription(DESCRIPTION);
        question.setCreationDate(DATE);
        question.setViews(VIEWS);

        return question;
    }

    private QuestionDto createQuestionDto() {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(ID);
        questionDto.setAuthor(AUTHOR);
        questionDto.setTitle(TITLE);
        questionDto.setDescription(DESCRIPTION);
        questionDto.setCreationDate(DATE);
        questionDto.setViews(VIEWS);

        return questionDto;
    }

    private User createUser() {
        User user = new User();
        user.setId(ID);
        user.setUserName(AUTHOR);

        return user;
    }

    private Answer createAnswerEntity() {
        Answer answer = new Answer();
        answer.setId(ID);
        answer.setContent("Sample content");

        return answer;
    }
}
