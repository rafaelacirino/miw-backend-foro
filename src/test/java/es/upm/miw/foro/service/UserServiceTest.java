package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.converter.UserMapper;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import es.upm.miw.foro.service.impl.JwtService;
import es.upm.miw.foro.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestConfig
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final LocalDateTime REGISTRED_DATE = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRole(Role.ADMIN);
        userDto.setRegisteredDate(REGISTRED_DATE);

        User user = new User();
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(Role.ADMIN);
        user.setRegisteredDate(REGISTRED_DATE);
    }

    @Test
    void testCreateUser_success() {
        // Arrange
        User authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword("encodedPassword");

        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDto createdUser = userService.createUser(userDtoInput);

        // Assert
        assertNotNull(createdUser);
        assertEquals(EMAIL, createdUser.getEmail());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(PASSWORD);
    }


    @Test
    void testRegisterUser_success() {
        // Arrange
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);

        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDto registeredUser = userService.registerUser(userDtoInput);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(EMAIL, registeredUser.getEmail());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(PASSWORD);
    }

    @Test
    void testGetUserById_success() {
        SecurityContextHolder.clearContext();

        User authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(authenticatedUser));

        UserDto foundUser = userService.getUserById(USER_ID);

        assertNotNull(foundUser);
        assertEquals(USER_ID, foundUser.getId());
    }

    @Test
    void testGetUserById_notFound() {
        User authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.getUserById(USER_ID));
        assertEquals("User with ID " + USER_ID + " not found", exception.getMessage());
    }

    @Test
    void testLogin_success() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(Role.ADMIN);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);
        when(jwtService.createToken(any(), any(), any())).thenReturn("jwtToken");

        String token = userService.login(EMAIL, PASSWORD);

        assertEquals("jwtToken", token);
    }

    @Test
    void testGetAllUsers_success() {
        User authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        User user = new User();
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<UserDto> result = userService.getAllUsers(null, null, null, mock(Pageable.class));

        assertFalse(result.isEmpty());
    }

    @Test
    void testUpdateUser_success() {
        // Arrange
        User authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        User existingUser = new User();
        existingUser.setId(USER_ID);
        existingUser.setEmail("old@email.com");
        existingUser.setRole(Role.MEMBER);

        UserDto userDtoInput = new UserDto();
        userDtoInput.setId(USER_ID);
        userDtoInput.setFirstName("UpdatedFirstName");
        userDtoInput.setLastName("UpdatedLastName");
        userDtoInput.setEmail("updated@email.com");
        userDtoInput.setPassword("newPassword");
        userDtoInput.setRole(Role.ADMIN);

        User updatedUser = new User();
        updatedUser.setId(USER_ID);
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setLastName("UpdatedLastName");
        updatedUser.setEmail("updated@email.com");
        updatedUser.setPassword("newPassword");
        updatedUser.setRole(Role.ADMIN);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDto updatedUserDto = userService.updateUser(USER_ID, userDtoInput);

        // Assert
        assertNotNull(updatedUserDto);
        assertEquals(USER_ID, updatedUserDto.getId());
        assertEquals("UpdatedFirstName", updatedUserDto.getFirstName());
        assertEquals("UpdatedLastName", updatedUserDto.getLastName());
        assertEquals("updated@email.com", updatedUserDto.getEmail());

        // Verify
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser_success() {
        User authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        doNothing().when(userRepository).deleteById(USER_ID);

        assertDoesNotThrow(() -> userService.deleteUser(USER_ID));

        verify(userRepository, times(1)).deleteById(USER_ID);
    }

    @Test
    void testDeleteUser_notFound() {
        User authenticatedUser = new User();
        authenticatedUser.setId(USER_ID);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        when(userRepository.existsById(USER_ID)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.deleteUser(USER_ID));
        assertEquals("User with id " + USER_ID + " not found", exception.getMessage());
    }
}
