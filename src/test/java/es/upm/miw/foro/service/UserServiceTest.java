package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.converter.UserMapper;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import es.upm.miw.foro.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private UserServiceImpl userService;

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final LocalDateTime REGISTRED_DATE = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setPhone(PHONE);
        userDto.setAddress(ADDRESS);
        userDto.setRole(Role.ADMIN);
        userDto.setRegisteredDate(REGISTRED_DATE);

        User user = new User();
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setPhone(PHONE);
        user.setAddress(ADDRESS);
        user.setRole(Role.ADMIN);
        user.setRegisteredDate(REGISTRED_DATE);
    }

    @Test
    void testDeleteUser_success() {
        // Mock
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        doNothing().when(userRepository).deleteById(USER_ID);

        // Assert
        assertDoesNotThrow(() -> userService.deleteUser(USER_ID));

        // Verify
        verify(userRepository, times(1)).existsById(USER_ID);
        verify(userRepository, times(1)).deleteById(USER_ID);
    }

    @Test
    void testDeleteUser_notFound() {
        // Mock
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // Arrange
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.deleteUser(USER_ID));

        // Assert
        assertEquals("User with id " + USER_ID + " not found", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).existsById(USER_ID);
    }

    @Test
    void testDeleteUser_repositoyException() {
        // Mock
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        doThrow(new DataAccessException("Error") {}).when(userRepository).deleteById(USER_ID);

        // Arrange
        RepositoryException exception = assertThrows(RepositoryException.class, () -> userService.deleteUser(USER_ID));

        // Assert
        assertEquals("Error deleting User with id " + USER_ID, exception.getMessage());

        // Verify
        verify(userRepository, times(1)).existsById(USER_ID);
    }
}
