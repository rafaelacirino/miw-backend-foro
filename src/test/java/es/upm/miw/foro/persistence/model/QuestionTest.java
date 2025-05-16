package es.upm.miw.foro.persistence.model;

import es.upm.miw.foro.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class QuestionTest {

    @Mock
    private User mockUser;

    @Mock
    private Answer mockAnswer1;

    @Mock
    private Answer mockAnswer2;

    private Question question;

    private static final Long ID = 1L;

    private final Integer views = 0;
    Set<String> viewedBySessions = new HashSet<>();
    Set<Long> viewedByUsers = new HashSet<>();

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        mockAnswer1 = mock(Answer.class);
        mockAnswer2 = mock(Answer.class);
        question = new Question();
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        question = new Question();

        // Assert
        assertNull(question.getId());
        assertNull(question.getAuthor());
        assertNull(question.getTitle());
        assertNull(question.getDescription());
        assertNull(question.getCreationDate());
        assertNotNull(question.getAnswers());
        assertTrue(question.getAnswers().isEmpty());
        assertNotNull(question.getViews());
        assertEquals(0, question.getViewedBySessions().size());
        assertEquals(0, question.getViewedByUsers().size());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        LocalDateTime creationDate = LocalDateTime.now();
        List<Answer> answers = new ArrayList<>();
        answers.add(mockAnswer1);

        question = new Question(
                ID, mockUser, "How to implement JPA?", "I need help with JPA in Spring Boot.",
                creationDate, answers, views, viewedBySessions, viewedByUsers);

        // Assert
        assertEquals(ID, question.getId());
        assertEquals(mockUser, question.getAuthor());
        assertEquals("How to implement JPA?", question.getTitle());
        assertEquals("I need help with JPA in Spring Boot.", question.getDescription());
        assertEquals(creationDate, question.getCreationDate());
        assertEquals(1, question.getAnswers().size());
        assertTrue(question.getAnswers().contains(mockAnswer1));
        assertEquals(views, question.getViews());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        LocalDateTime creationDate = LocalDateTime.now();
        List<Answer> answers = new ArrayList<>();
        answers.add(mockAnswer1);

        question = new Question();
        question.setId(ID);
        question.setAuthor(mockUser);
        question.setTitle("How to implement JPA?");
        question.setDescription("I need help with JPA in Spring Boot.");
        question.setCreationDate(creationDate);
        question.setAnswers(answers);
        question.setViews(views);

        // Assert
        assertEquals(ID, question.getId());
        assertEquals(mockUser, question.getAuthor());
        assertEquals("How to implement JPA?", question.getTitle());
        assertEquals("I need help with JPA in Spring Boot.", question.getDescription());
        assertEquals(creationDate, question.getCreationDate());
        assertEquals(1, question.getAnswers().size());
        assertTrue(question.getAnswers().contains(mockAnswer1));
        assertEquals(views, question.getViews());
    }

    @Test
    void testAddAnswer() {
        // Arrange
        question = new Question();

        // Act
        question.addAnswer(mockAnswer1);

        // Assert
        assertNotNull(question.getAnswers());
        assertEquals(1, question.getAnswers().size());
        assertTrue(question.getAnswers().contains(mockAnswer1));
        verify(mockAnswer1).setQuestion(question);
    }

    @Test
    void testRemoveAnswer() {
        // Arrange
        question = new Question();
        question.addAnswer(mockAnswer1);
        question.addAnswer(mockAnswer2);

        // Act
        question.removeAnswer(mockAnswer1);

        // Assert
        assertNotNull(question.getAnswers());
        assertEquals(1, question.getAnswers().size());
        assertFalse(question.getAnswers().contains(mockAnswer1));
        assertTrue(question.getAnswers().contains(mockAnswer2));
        verify(mockAnswer1).setQuestion(null);
    }

    @Test
    void testIncrementViewsIfNew_withSessionId() {
        // Act
        boolean result = question.incrementViewsIfNew("session-123", null);

        // Assert
        assertTrue(result);
        assertEquals(1, question.getViews());
        assertTrue(question.getViewedBySessions().contains("session-123"));
    }

    @Test
    void testIncrementViewsIfNew_withUserId() {
        // Act
        boolean result = question.incrementViewsIfNew(null, 5L);

        // Assert
        assertTrue(result);
        assertEquals(1, question.getViews());
        assertTrue(question.getViewedByUsers().contains(5L));
    }

    @Test
    void testIncrementViewsIfAlreadyViewed() {
        // Arrange
        question.incrementViewsIfNew("session-123", null);

        // Act
        boolean result = question.incrementViewsIfNew("session-123", null);

        // Assert
        assertFalse(result);
        assertEquals(1, question.getViews()); // no incrementa
    }

    @Test
    void testOnCreate_setsCreationDate() {
        // Arrange
        question = new Question();
        assertNull(question.getCreationDate());

        // Act
        question.onCreate();

        // Assert
        assertNotNull(question.getCreationDate());
        assertTrue(question.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(question.getCreationDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testToString() {
        // Arrange
        LocalDateTime creationDate = LocalDateTime.of(2025, 1, 1, 10, 0);
        List<Answer> answers = new ArrayList<>();
        answers.add(mockAnswer1);

        question = new Question(
                ID, mockUser, "How to implement...", "I need help with...",
                creationDate, answers, views, viewedBySessions, viewedByUsers);

        when(mockUser.toString()).thenReturn("User(id=1, firstName=Alex, lastName=Ye)");
        when(mockAnswer1.toString()).thenReturn("Answer(id=1, content=Use @Entity)");

        String expectedString = "Question(id=1, author=User(id=1, firstName=Alex, lastName=Ye), " +
                "title=How to implement..., description=I need help with..., " +
                "creationDate=2025-01-01T10:00, answers=[Answer(id=1, content=Use @Entity)], " +
                "views=0, viewedBySessions=[], viewedByUsers=[])";

        // Act & Assert
        assertEquals(expectedString, question.toString());
    }
}
