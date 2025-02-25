package es.upm.miw.foro.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServiceExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String expectedMessage = "Service exception occurred";

        // Act
        ServiceException serviceException = new ServiceException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, serviceException.getMessage());
        assertNull(serviceException.getCause());
    }

    @Test
    void testConstructorWithCause() {
        // Arrange
        Throwable cause = new Throwable("Invalid data");

        // Act
        ServiceException serviceException = new ServiceException(cause);

        // Assert
        assertEquals("Invalid data", serviceException.getCause().getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange
        String expectedMessage = "Service exception occurred";
        Throwable cause = new Throwable("Invalid data");

        // Act
        ServiceException serviceException = new ServiceException(expectedMessage, cause);

        // Assert
        assertEquals(expectedMessage, serviceException.getMessage());
        assertEquals("Invalid data", serviceException.getCause().getMessage());
    }
}
