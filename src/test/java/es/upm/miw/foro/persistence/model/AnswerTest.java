package es.upm.miw.foro.persistence.model;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfig
class AnswerTest {

    private static final Long ANSWER_ID = 1L;

    @Mock
    private Question mockQuestion;

    @Mock
    private User mockUser;

    private Answer answer;

    @BeforeEach
    void setUp() {
        mockQuestion = mock(Question.class);
        mockUser = mock(User.class);
        answer = new Answer();
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        answer = new Answer();

        // Assert
        assertNull(answer.getId());
        assertNull(answer.getContent());
        assertNull(answer.getQuestion());
        assertNull(answer.getAuthor());
        assertNull(answer.getCreationDate());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        LocalDateTime creationDate = LocalDateTime.now();

        answer = new Answer(
                ANSWER_ID, "Use @Entity and @Id annotations...", mockQuestion, mockUser, creationDate);

        // Assert
        assertEquals(ANSWER_ID, answer.getId());
        assertEquals("Use @Entity and @Id annotations...", answer.getContent());
        assertEquals(mockQuestion, answer.getQuestion());
        assertEquals(mockUser, answer.getAuthor());
        assertEquals(creationDate, answer.getCreationDate());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        LocalDateTime creationDate = LocalDateTime.now();

        answer = new Answer();
        answer.setId(ANSWER_ID);
        answer.setContent("Use @Entity and @Id annotations...");
        answer.setQuestion(mockQuestion);
        answer.setAuthor(mockUser);
        answer.setCreationDate(creationDate);

        // Assert
        assertEquals(ANSWER_ID, answer.getId());
        assertEquals("Use @Entity and @Id annotations...", answer.getContent());
        assertEquals(mockQuestion, answer.getQuestion());
        assertEquals(mockUser, answer.getAuthor());
        assertEquals(creationDate, answer.getCreationDate());
    }

    @Test
    void testOnCreate_setsCreationDate() {
        // Arrange
        answer = new Answer();
        assertNull(answer.getCreationDate());

        // Act
        answer.onCreate();

        // Assert
        assertNotNull(answer.getCreationDate());
        assertTrue(answer.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(answer.getCreationDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testToString() {
        // Arrange
        LocalDateTime creationDate = LocalDateTime.of(2025, 1, 1, 10, 0);

        answer = new Answer(
                ANSWER_ID, "Use @Entity and @Id annotations...", mockQuestion, mockUser, creationDate);

        when(mockQuestion.toString()).thenReturn("Question(id=1, title=How to implement...)");
        when(mockUser.toString()).thenReturn("User(id=1, firstName=Alex, lastName=Ye)");

        String expectedString = "Answer(id=1, content=Use @Entity and @Id annotations..., " +
                "question=Question(id=1, title=How to implement...), author=User(id=1, firstName=Alex, lastName=Ye), " +
                "creationDate=2025-01-01T10:00)";

        // Act & Assert
        assertEquals(expectedString, answer.toString());
    }
}
