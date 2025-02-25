package es.upm.miw.foro.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RepositoryExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String expectedMessage = "Error occurred";

        // Act
        RepositoryException exception = new RepositoryException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithCause() {
        // Arrange
        Throwable cause = new Throwable("Database connection failed");

        // Act
        RepositoryException exception = new RepositoryException(cause);

        // Assert
        assertEquals("Database connection failed", exception.getCause().getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange
        String expectedMessage = "Error occurred";
        Throwable cause = new Throwable("Database connection failed");

        // Act
        RepositoryException exception = new RepositoryException(expectedMessage, cause);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals("Database connection failed", exception.getCause().getMessage());
    }
}
