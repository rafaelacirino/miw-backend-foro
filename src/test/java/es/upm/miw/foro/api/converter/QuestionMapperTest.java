package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.QuestionDto;
import es.upm.miw.foro.persistance.model.Question;
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

    @Test
    void testToConstructorThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, QuestionMapper::new);
    }

    @Test
    void toQuestionDto_shouldMapEntityToDto() {
        // Arrange
        Question question = createQuestionEntity();

        // Act
        QuestionDto questionDto = QuestionMapper.toQuestionDto(question);

        // Assert
        assertNotNull(questionDto);
        assertEquals(question.getId(), questionDto.getId());
        assertEquals(question.getAuthor(), questionDto.getAuthor());
        assertEquals(question.getTitle(), questionDto.getTitle());
        assertEquals(question.getDescription(), questionDto.getDescription());
        assertEquals(question.getCreationDate(), questionDto.getCreationDate());
        assertEquals(question.getWasRead(), questionDto.getWasRead());
    }

    @Test
    void testEntity_shouldMapDtoToEntity() {
        // Arrange
        QuestionDto questionDto = createQuestionDto();

        // Act
        Question question = QuestionMapper.toEntity(questionDto);

        // Assert
        assertNotNull(question);
        assertEquals(questionDto.getId(), question.getId());
        assertEquals(questionDto.getAuthor(), question.getAuthor());
        assertEquals(questionDto.getTitle(), question.getTitle());
        assertEquals(questionDto.getDescription(), question.getDescription());
        assertEquals(questionDto.getCreationDate(), question.getCreationDate());
        assertEquals(questionDto.getWasRead(), question.getWasRead());
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

        // Act
        List<Question> entityList = QuestionMapper.toEntityList(dtoList);

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
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        // Act
        Question question = QuestionMapper.toEntity(null);

        // Assert
        assertNull(question);
    }

    private Question createQuestionEntity() {
        Question question = new Question();
        question.setId(ID);
        question.setAuthor(AUTHOR);
        question.setTitle(TITLE);
        question.setDescription(DESCRIPTION);
        question.setCreationDate(DATE);
        question.setWasRead(true);

        return question;
    }

    private QuestionDto createQuestionDto() {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(ID);
        questionDto.setAuthor(AUTHOR);
        questionDto.setTitle(TITLE);
        questionDto.setDescription(DESCRIPTION);
        questionDto.setCreationDate(DATE);
        questionDto.setWasRead(true);

        return questionDto;
    }
}
