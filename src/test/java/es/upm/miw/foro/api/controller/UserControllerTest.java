package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private static final LocalDateTime REGISTRED_DATE = LocalDateTime.now();

    @Test
    void testCreateUser() {
        UserDto dto =  new UserDto();
        when(userService.createUser(any(UserDto.class))).thenReturn(dto);

        ResponseEntity<UserDto> response = this.userController.createUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testRegisterUser() {
        UserDto dto =  new UserDto();
        when(userService.registerUser(any(UserDto.class))).thenReturn(dto);

        ResponseEntity<UserDto> response = this.userController.registerUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetUserById() {
        UserDto dto =  new UserDto();
        when(userService.getUserById(USER_ID)).thenReturn(dto);

        ResponseEntity<UserDto> response = this.userController.getUserById(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
    }

//    @Test
//    void testGetUserByEmailSuccess() throws ServiceException {
//        UserDto dto =  new UserDto();
//        when(userService.getUserByEmail(EMAIL)).thenReturn(dto);
//
//        ResponseEntity<UserDto> response = this.userController.getAuthenticatedUser();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(dto, response.getBody());
//    }
//
//    @Test
//    void testGetUserByEmail_UserNotFound() {
//    }

//    @Test
//    void testGetAuthenticatedUser_Success() throws ServiceException {
//        // Preparar datos de prueba
//        String email = "test@example.com";
//        UserDto userDto = new UserDto();
//        userDto.setId(1L);
//        userDto.setEmail(email);
//        userDto.setFirstName("Test");
//        userDto.setLastName("User");
//
//        // Configurar el contexto de autenticación
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn(email);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Simular el comportamiento del servicio
//        when(userService.getUserByEmail(email)).thenReturn(userDto);
//
//        // Ejecutar el método
//        ResponseEntity<UserDto> response = userController.getAuthenticatedUser();
//
//        // Verificar resultados
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1L, response.getBody().getId());
//        assertEquals("Test", response.getBody().getFirstName());
//        assertEquals("User", response.getBody().getLastName());
//
//        // Verificar interacciones
//        verify(userService, times(1)).getUserByEmail(email);
//    }
//
//    @Test
//    void testGetAuthenticatedUser_Unauthorized() {
//        // Configurar un contexto de autenticación vacío
//        SecurityContextHolder.clearContext();
//
//        // Ejecutar el método
//        ResponseEntity<UserDto> response = userController.getAuthenticatedUser();
//
//        // Verificar resultados
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void testGetAuthenticatedUser_UserNotFound() throws ServiceException {
//        // Configurar el contexto de autenticación
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("nonexistent@example.com");
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Simular que el usuario no existe
//        when(userService.getUserByEmail("nonexistent@example.com")).thenThrow(new ServiceException("User not found"));
//
//        // Ejecutar el método
//        ResponseEntity<UserDto> response = userController.getAuthenticatedUser();
//
//        // Verificar resultados
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertNull(response.getBody());
//    }

    @Test
    void testGetAllUsers() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRole(Role.ADMIN);
        userDto.setRegisteredDate(REGISTRED_DATE);
        Page<UserDto> userPage = new PageImpl<>(List.of(userDto));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(userService.getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, pageable)).thenReturn(userPage);

        // Act
        ResponseEntity<Page<UserDto>> response = userController.getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, 0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userService, times(1)).getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, pageable);
    }

    @Test
    void testGetAllUsersWithFiltersDescendingFilter() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRole(Role.ADMIN);
        userDto.setRegisteredDate(REGISTRED_DATE);
        Page<UserDto> userPage = new PageImpl<>(List.of(userDto));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        when(userService.getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, pageable)).thenReturn(userPage);

        // Act
        ResponseEntity<Page<UserDto>> response = userController.getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, 0, 10, "id", "desc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userService, times(1)).getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, pageable);
    }

    @Test
    void testUpdateUser() {
        UserDto dto = new UserDto();

        when(userService.updateUser(eq(USER_ID), any(UserDto.class))).thenReturn(dto);

        ResponseEntity<UserDto> response = this.userController.updateUser(USER_ID, dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(USER_ID);

        ResponseEntity<Void> response = userController.deleteUser(USER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(USER_ID);
    }
}
