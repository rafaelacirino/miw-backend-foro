package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.LoginDto;
import es.upm.miw.foro.api.dto.TokenDto;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final String UNEXPECTED_ERROR = "Unexpected error";

    @Test
    void testCreateUser() {
        UserDto dto =  new UserDto();
        when(userService.createUser(any(UserDto.class))).thenReturn(dto);

        ResponseEntity<Object> response = this.userController.createUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(userService, times(1)).createUser(dto);
    }

    @Test
    void testCreateUserServiceException() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "User creation failed";
        when(userService.createUser(any(UserDto.class))).thenThrow(new ServiceException("User creation failed"));

        // Act
        ResponseEntity<Object> response = this.userController.createUser(dto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService, times(1)).createUser(dto);
    }

    @Test
    void testCreateUserServiceExceptionWithCustomStatus() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "Invalid user data";
        HttpStatus customStatus = HttpStatus.BAD_REQUEST;
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new ServiceException(errorMessage, customStatus));

        // Act
        ResponseEntity<Object> response = this.userController.createUser(dto);

        // Assert
        assertEquals(customStatus, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService, times(1)).createUser(dto);
    }

    @Test
    void testCreateUserGenericException() {
        // Arrange
        UserDto dto = new UserDto();
        when(userService.createUser(any(UserDto.class))).thenThrow(new RuntimeException(UNEXPECTED_ERROR));

        // Act
        ResponseEntity<Object> response = this.userController.createUser(dto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(UNEXPECTED_ERROR, response.getBody());
        verify(userService, times(1)).createUser(dto);
    }

    @Test
    void testRegisterUser() {
        UserDto dto =  new UserDto();
        when(userService.registerUser(any(UserDto.class))).thenReturn(dto);

        ResponseEntity<Object> response = this.userController.registerUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testRegisterUserServiceException() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "User registration failed";
        when(userService.registerUser(any(UserDto.class))).thenThrow(new ServiceException(errorMessage, HttpStatus.CONFLICT));

        // Act
        ResponseEntity<Object> response = this.userController.registerUser(dto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals(errorMessage, ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(userService, times(1)).registerUser(dto);
    }

    @Test
    void testRegisterUserWithBadRequestError() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "Email already registered";
        when(userService.registerUser(any(UserDto.class)))
                .thenThrow(new ServiceException(errorMessage, HttpStatus.BAD_REQUEST));

        // Act
        ResponseEntity<Object> response = this.userController.registerUser(dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals(errorMessage, ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(userService, times(1)).registerUser(dto);
    }

    @Test
    void testRegisterUserGenericException() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "An unexpected error occurred";
        when(userService.registerUser(any(UserDto.class))).thenThrow(new RuntimeException(UNEXPECTED_ERROR));

        // Act
        ResponseEntity<Object> response = this.userController.registerUser(dto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals(errorMessage, ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(userService, times(1)).registerUser(dto);
    }

    @Test
    void testGetUserById() {
        UserDto dto =  new UserDto();
        when(userService.getUserById(USER_ID)).thenReturn(dto);

        ResponseEntity<UserDto> response = this.userController.getUserById(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(userService, times(1)).getUserById(USER_ID);
    }

    @Test
    void testGetUserByIdServiceException() {
        // Arrange
        when(userService.getUserById(USER_ID)).thenThrow(new ServiceException("User not authorized"));

        // Act
        ResponseEntity<UserDto> response = this.userController.getUserById(USER_ID);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(USER_ID);
    }

    @Test
    void testGetUserByIdGenericException() {
        // Arrange
        when(userService.getUserById(USER_ID)).thenThrow(new RuntimeException(UNEXPECTED_ERROR));

        // Act
        ResponseEntity<UserDto> response = this.userController.getUserById(USER_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(USER_ID);
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        String token = UUID.randomUUID().toString();
        LoginDto loginDto = new LoginDto(EMAIL, PASSWORD);
        when(userService.login(EMAIL, PASSWORD)).thenReturn(token);

        // Act
        ResponseEntity<?> response = userController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(TokenDto.class, response.getBody());
        TokenDto tokenDto = (TokenDto) response.getBody();
        assertEquals(token, tokenDto.getToken());
        verify(userService, times(1)).login(EMAIL, PASSWORD);
    }

    @Test
    void testLoginServiceException() {
        // Arrange
        String errorMessage = "Incorrect password";
        LoginDto loginDto = new LoginDto(EMAIL, PASSWORD);
        when(userService.login(EMAIL, PASSWORD)).thenThrow(new ServiceException(errorMessage, HttpStatus.UNAUTHORIZED));

        // Act
        ResponseEntity<?> response = userController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals(errorMessage, ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(userService, times(1)).login(EMAIL, PASSWORD);
    }

   @Test
    void testGetAllUsers() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        Page<UserDto> userPage = new PageImpl<>(List.of(userDto));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(userService.getAllUsers(null, null, null, pageable)).thenReturn(userPage);

        // Act
        ResponseEntity<Page<UserDto>> response = userController.getAllUsers(null, null, null,
                                                                    0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userService, times(1))
                                                        .getAllUsers(null, null, null, pageable);
    }

    @Test
    void testGetAllUsersWithDescending() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        Page<UserDto> userPage = new PageImpl<>(List.of(userDto));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        when(userService.getAllUsers(null, null, null, pageable)).thenReturn(userPage);

        // Act
        ResponseEntity<Page<UserDto>> response = userController.getAllUsers(null, null, null,
                                                                    0, 10, "id", "desc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userService, times(1))
                .getAllUsers(null, null, null, pageable);
    }

   @Test
    void testGetAllUsers_withFilters() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        Page<UserDto> userPage = new PageImpl<>(List.of(userDto));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(userService.getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, pageable)).thenReturn(userPage);

        // Act
        ResponseEntity<Page<UserDto>> response = userController.getAllUsers(FIRST_NAME, LAST_NAME, EMAIL,
                                                                        0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userService, times(1)).getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, pageable);
    }

    @Test
    void testGetAllUsersServiceException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(userService.getAllUsers(null, null, null, pageable))
                .thenThrow(new ServiceException("Unauthorized: Only admins can list users"));

        // Act
        ResponseEntity<Page<UserDto>> response = userController.getAllUsers(null, null, null,
                0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getAllUsers(null, null, null, pageable);
    }

    @Test
    void testGetAllUsersGenericException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(userService.getAllUsers(null, null, null, pageable))
                .thenThrow(new RuntimeException(UNEXPECTED_ERROR));

        // Act
        ResponseEntity<Page<UserDto>> response = userController.getAllUsers(null, null, null,
                0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getAllUsers(null, null, null, pageable);
    }

    @Test
    void testUpdateUser() {
        UserDto dto = new UserDto();

        when(userService.updateUser(eq(USER_ID), any(UserDto.class))).thenReturn(dto);

        ResponseEntity<Object> response = this.userController.updateUser(USER_ID, dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testUpdateUserServiceException() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "User update failed";
        when(userService.updateUser(eq(USER_ID), any(UserDto.class))).thenThrow(new ServiceException(errorMessage));

        // Act
        ResponseEntity<Object> response = this.userController.updateUser(USER_ID, dto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService, times(1)).updateUser(USER_ID, dto);
    }

    @Test
    void testUpdateUserServiceExceptionWithCustomStatus() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "User not found";
        HttpStatus customStatus = HttpStatus.NOT_FOUND;
        when(userService.updateUser(eq(USER_ID), any(UserDto.class)))
                .thenThrow(new ServiceException(errorMessage, customStatus));

        // Act
        ResponseEntity<Object> response = this.userController.updateUser(USER_ID, dto);

        // Assert
        assertEquals(customStatus, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(userService, times(1)).updateUser(USER_ID, dto);
    }

    @Test
    void testUpdateUserGenericException() {
        // Arrange
        UserDto dto = new UserDto();
        String errorMessage = "An unexpected error occurred";
        when(userService.updateUser(eq(USER_ID), any(UserDto.class)))
                .thenThrow(new RuntimeException(UNEXPECTED_ERROR));

        // Act
        ResponseEntity<Object> response = this.userController.updateUser(USER_ID, dto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals(errorMessage, ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(userService, times(1)).updateUser(USER_ID, dto);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(USER_ID);

        ResponseEntity<Void> response = userController.deleteUser(USER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(USER_ID);
    }

    @Test
    void testDeleteUserNotFound() {
        // Arrange
        String errorMessage = "User with id " + USER_ID + " not found";
        doThrow(new ServiceException(errorMessage)).when(userService).deleteUser(USER_ID);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(USER_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).deleteUser(USER_ID);
    }

    @Test
    void testDeleteUserOtherServiceException() {
        // Arrange
        String errorMessage = "Unauthorized: Only admins or the user themselves can delete this user";
        doThrow(new ServiceException(errorMessage)).when(userService).deleteUser(USER_ID);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(USER_ID);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userService, times(1)).deleteUser(USER_ID);
    }
}
