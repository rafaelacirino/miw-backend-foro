package es.upm.miw.foro.api.dto;

import es.upm.miw.foro.persistance.model.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDtoTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final LocalDateTime REGISTERED_DATE = LocalDateTime.now();

    @Test
    void testUserDtoBuilderAndGetters() {
        // Act
        UserDto dto = UserDto.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .role(Role.ADMIN)
                .registeredDate(REGISTERED_DATE)
                .build();

        // Assert
        assertEquals(USER_ID, dto.getId());
        assertEquals(FIRST_NAME, dto.getFirstName());
        assertEquals(LAST_NAME, dto.getLastName());
        assertEquals(EMAIL, dto.getEmail());
        assertEquals(PASSWORD, dto.getPassword());
        assertEquals(Role.ADMIN, dto.getRole());
        assertEquals(REGISTERED_DATE, dto.getRegisteredDate());
    }

    @Test
    void testUserDtoSetters() {
        // Arrange
        UserDto dto = new UserDto();

        // Act
        dto.setId(USER_ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setRole(Role.ADMIN);
        dto.setRegisteredDate(REGISTERED_DATE);

        // Assert
        assertEquals(USER_ID, dto.getId());
        assertEquals(FIRST_NAME, dto.getFirstName());
        assertEquals(LAST_NAME, dto.getLastName());
        assertEquals(EMAIL, dto.getEmail());
        assertEquals(PASSWORD, dto.getPassword());
        assertEquals(Role.ADMIN, dto.getRole());
        assertEquals(REGISTERED_DATE, dto.getRegisteredDate());
    }

    @Test
    void testToString() {
        // Arrange
        UserDto dto = UserDto.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();

        // Act
        String dtoString = dto.toString();

        // Assert
        assertTrue(dtoString.contains("UserDto"));
        assertTrue(dtoString.contains("id=" + USER_ID));
        assertTrue(dtoString.contains("firstName=" + FIRST_NAME));
        assertTrue(dtoString.contains("lastName=" + LAST_NAME));
    }
}
