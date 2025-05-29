package es.upm.miw.foro.persistence.model;

import es.upm.miw.foro.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfig
class TagTest {

    private Tag tag;
    private static final Long ID = 1L;
    private static final String NAME = "java";

    private Question mockQuestion1;
    private Question mockQuestion2;
    private List<Question> questions;

    @BeforeEach
    void setUp() {
        mockQuestion1 = mock(Question.class);
        mockQuestion2 = mock(Question.class);
        questions = new ArrayList<>();
        questions.add(mockQuestion1);
        questions.add(mockQuestion2);
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange
        tag = new Tag();

        // Assert
        assertNull(tag.getId());
        assertNull(tag.getName());
        assertNotNull(tag.getQuestions());
        assertTrue(tag.getQuestions().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        tag = new Tag(ID, NAME, questions);

        // Assert
        assertEquals(ID, tag.getId());
        assertEquals(NAME, tag.getName());
        assertEquals(2, tag.getQuestions().size());
        assertTrue(tag.getQuestions().contains(mockQuestion1));
        assertTrue(tag.getQuestions().contains(mockQuestion2));
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        tag = new Tag();

        tag.setId(ID);
        tag.setName(NAME);
        tag.setQuestions(questions);

        // Assert
        assertEquals(ID, tag.getId());
        assertEquals(NAME, tag.getName());
        assertEquals(2, tag.getQuestions().size());
        assertTrue(tag.getQuestions().contains(mockQuestion1));
    }

    @Test
    void testToString() {
        // Arrange
        when(mockQuestion1.toString()).thenReturn("Question(id=1, title=Test Question 1)");
        when(mockQuestion2.toString()).thenReturn("Question(id=2, title=Test Question 2)");

        questions.add(mockQuestion1);
        questions.add(mockQuestion2);

        tag = new Tag(1L, "java", questions);

        // Act
        String result = tag.toString();

        // Assert
        assertTrue(result.contains("Tag"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name=java"));
        assertTrue(result.contains("Question(id=1, title=Test Question 1)"));
        assertTrue(result.contains("Question(id=2, title=Test Question 2)"));
    }
}
