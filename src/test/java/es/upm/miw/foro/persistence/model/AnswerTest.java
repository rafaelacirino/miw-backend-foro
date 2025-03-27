package es.upm.miw.foro.persistence.model;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class AnswerTest {

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
                1L, "Use @Entity and @Id annotations...", mockQuestion, mockUser, creationDate);

        // Assert
        assertEquals(1L, answer.getId());
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
        answer.setId(1L);
        answer.setContent("Use @Entity and @Id annotations...");
        answer.setQuestion(mockQuestion);
        answer.setAuthor(mockUser);
        answer.setCreationDate(creationDate);

        // Assert
        assertEquals(1L, answer.getId());
        assertEquals("Use @Entity and @Id annotations...", answer.getContent());
        assertEquals(mockQuestion, answer.getQuestion());
        assertEquals(mockUser, answer.getAuthor());
        assertEquals(creationDate, answer.getCreationDate());
    }

    @Test
    void testSetQuestion_addsAnswerToQuestion() {
        // Arrange
        List<Answer> answers = new ArrayList<>();
        when(mockQuestion.getAnswers()).thenReturn(answers);

        // Act
        answer.setQuestion(mockQuestion);

        // Assert
        assertNotNull(answer.getQuestion());
        assertEquals(mockQuestion, answer.getQuestion());
        assertTrue(answers.contains(answer));
        verify(mockQuestion).getAnswers();
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
        LocalDateTime creationDate = LocalDateTime.of(2023, 1, 1, 10, 0);

        answer = new Answer(
                1L, "Use @Entity and @Id annotations...", mockQuestion, mockUser, creationDate);

        when(mockQuestion.toString()).thenReturn("Question(id=1, title=How to implement JPA?)");
        when(mockUser.toString()).thenReturn("User(id=1, firstName=John, lastName=Doe)");

        String expectedString = "Answer(id=1, content=Use @Entity and @Id annotations..., " +
                "question=Question(id=1, title=How to implement JPA?), author=User(id=1, firstName=John, lastName=Doe), " +
                "creationDate=2023-01-01T10:00)";

        // Act & Assert
        assertEquals(expectedString, answer.toString());
    }
}
