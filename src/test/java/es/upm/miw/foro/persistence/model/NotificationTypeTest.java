package es.upm.miw.foro.persistence.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationTypeTest {

    @Test
    void testEnumValues() {
        NotificationType[] expectedValues = {NotificationType.QUESTION_REPLIED, NotificationType.ANSWER_RATED};
        NotificationType[] actualValues = NotificationType.values();

        assertArrayEquals(expectedValues, actualValues, "The values NotificationType do not correspond to the expected values.");
    }

    @Test
    void testEnumValueOf() {
        assertEquals(NotificationType.QUESTION_REPLIED, NotificationType.valueOf("QUESTION_REPLIED"), "QUESTION_REPLIED not found.");
        assertEquals(NotificationType.ANSWER_RATED, NotificationType.valueOf("ANSWER_RATED"), "ANSWER_RATED not found.");
    }

    @Test
    void testEnumName() {
        assertEquals("QUESTION_REPLIED", NotificationType.QUESTION_REPLIED.name(), "QUESTION_REPLIED is incorrect.");
        assertEquals("ANSWER_RATED", NotificationType.ANSWER_RATED.name(), "ANSWER_RATED is incorrect.");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, NotificationType.QUESTION_REPLIED.ordinal(), "The original position of QUESTION_REPLIED is incorrect.");
        assertEquals(1, NotificationType.ANSWER_RATED.ordinal(), "The original position of ANSWER_RATED is incorrect.");
    }
}
