package es.upm.miw.foro.persistence.model;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class UserTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertNull(user.getId());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getAddress());
        assertNull(user.getPhone());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getRole());
        assertNull(user.getRegisteredDate());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        LocalDateTime registeredDate = LocalDateTime.now();
        User user = new User(
                1L, "firstName", "lastName", "address", "phone",
                "email@email.com", "password", Role.ADMIN, registeredDate);

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals("address", user.getAddress());
        assertEquals("phone", user.getPhone());
        assertEquals("email@email.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(registeredDate, user.getRegisteredDate());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        User user = new User();
        LocalDateTime registeredDate = LocalDateTime.now();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setAddress("address");
        user.setPhone("phone");
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setRole(Role.ADMIN);
        user.setRegisteredDate(registeredDate);

        assertEquals(1L, user.getId());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals("address", user.getAddress());
        assertEquals("phone", user.getPhone());
        assertEquals("email@email.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(registeredDate, user.getRegisteredDate());
    }

    @Test
    void testPersistUser() {
        // Arrange
        LocalDateTime registeredDate = LocalDateTime.now();
        User user = new User(
                1L, "firstName", "lastName", "address", "phone",
                "email@email.com", "password", Role.ADMIN, registeredDate);

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getFirstName(), savedUser.getFirstName());
        assertEquals(user.getLastName(), savedUser.getLastName());
        assertEquals(user.getAddress(), savedUser.getAddress());
        assertEquals(user.getPhone(), savedUser.getPhone());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getPassword(), savedUser.getPassword());
        assertEquals(user.getRole(), savedUser.getRole());
        assertEquals(registeredDate, user.getRegisteredDate());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testToString() {
        // Arrange
        LocalDateTime registeredDate = LocalDateTime.of(2023, 1, 1, 10, 0);
        User user = new User(
                1L, "firstName", "lastName", "address", "phone",
                "email@email.com", "password", Role.ADMIN, registeredDate);

        String expectedString = "User(id=1, firstName=firstName, lastName=lastName, address=address, phone=phone, " +
                "email=email@email.com, password=password, role=ADMIN, registeredDate=2023-01-01T10:00)";

        // Act & Assert
        assertEquals(expectedString, user.toString());


    }
}
