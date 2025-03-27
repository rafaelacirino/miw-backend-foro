package es.upm.miw.foro.service;

import es.upm.miw.foro.TestConfig;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.api.dto.validation.UserValidation;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import es.upm.miw.foro.service.impl.JwtService;
import es.upm.miw.foro.service.impl.UserServiceImpl;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @Mock
    private Validator validator;

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String USERNAME = "username";
    private static final String PHONE_NUMBER = "UserPhoneNumber";
    private static final String EMAIL = "email@email.com";
    private static final String EMAIL_ADMIN = "admin@email.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final LocalDateTime REGISTERED_DATE = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setUserName(USERNAME);
        userDto.setPhone(PHONE_NUMBER);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRole(Role.ADMIN);
        userDto.setRegisteredDate(REGISTERED_DATE);

        user = new User();
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setUserName(USERNAME);
        user.setPhone(PHONE_NUMBER);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(Role.ADMIN);
        user.setRegisteredDate(REGISTERED_DATE);
    }

    @Test
    void testCreateUser_success() {
        // Arrange
        User authenticatedUser = new User();
        authenticatedUser.setId(2L);
        authenticatedUser.setEmail(EMAIL_ADMIN);
        authenticatedUser.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL_ADMIN);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(validator.validate(any(UserDto.class), eq(UserValidation.class))).thenReturn(Collections.emptySet()); // Mock do Validator

        User savedUser = new User();
        savedUser.setId(USER_ID);
        savedUser.setFirstName(FIRST_NAME);
        savedUser.setLastName(LAST_NAME);
        savedUser.setUserName(USERNAME);
        savedUser.setPhone(PHONE_NUMBER);
        savedUser.setEmail(EMAIL);
        savedUser.setPassword(ENCODED_PASSWORD);
        savedUser.setRole(Role.MEMBER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto userDtoInput = new UserDto();
        userDtoInput.setFirstName(FIRST_NAME);
        userDtoInput.setLastName(LAST_NAME);
        userDtoInput.setUserName(USERNAME);
        userDtoInput.setPhone(PHONE_NUMBER);
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        // Act
        UserDto createdUser = userService.createUser(userDtoInput);

        // Assert
        assertNotNull(createdUser);
        assertEquals(USER_ID, createdUser.getId());
        assertEquals(FIRST_NAME, createdUser.getFirstName());
        assertEquals(LAST_NAME, createdUser.getLastName());
        assertEquals(USERNAME, createdUser.getUserName());
        assertEquals(PHONE_NUMBER, createdUser.getPhone());
        assertEquals(EMAIL, createdUser.getEmail());
        assertEquals(Role.MEMBER, createdUser.getRole());

        // Verify
        verify(userRepository, times(1)).existsByEmail(EMAIL);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(PASSWORD);
        verify(validator, times(1)).validate(any(UserDto.class), eq(UserValidation.class));
    }

    @Test
    void testCreateUser_withNullRole() {
        // Arrange
        setupAuthentication();
        UserDto userDtoInput = new UserDto();
        userDtoInput.setFirstName(FIRST_NAME);
        userDtoInput.setLastName(LAST_NAME);
        userDtoInput.setUserName(USERNAME);
        userDtoInput.setPhone(PHONE_NUMBER);
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);
        userDtoInput.setRole(null);

        User savedUser = new User();
        savedUser.setId(USER_ID);
        savedUser.setFirstName(FIRST_NAME);
        savedUser.setLastName(LAST_NAME);
        savedUser.setUserName(USERNAME);
        savedUser.setPhone(PHONE_NUMBER);
        savedUser.setEmail(EMAIL);
        savedUser.setPassword(ENCODED_PASSWORD);
        savedUser.setRole(Role.MEMBER);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(validator.validate(any(UserDto.class), eq(UserValidation.class))).thenReturn(Collections.emptySet());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDto createdUser = userService.createUser(userDtoInput);

        // Assert
        assertNotNull(createdUser);
        assertEquals(Role.MEMBER, createdUser.getRole());

        // Verify
        verify(userRepository, times(1)).existsByEmail(EMAIL);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(PASSWORD);
        verify(validator, times(1)).validate(any(UserDto.class), eq(UserValidation.class));
    }

    @Test
    void testCreateUser_failure() {
        // Arrange
        User authenticatedUser = new User();
        authenticatedUser.setId(2L);
        authenticatedUser.setEmail(EMAIL_ADMIN);
        authenticatedUser.setFirstName(FIRST_NAME);
        authenticatedUser.setLastName(LAST_NAME);
        authenticatedUser.setUserName(USERNAME);
        authenticatedUser.setPhone(PHONE_NUMBER);
        authenticatedUser.setRole(Role.ADMIN);
        authenticatedUser.setRegisteredDate(REGISTERED_DATE);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL_ADMIN);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL_ADMIN)).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
        when(validator.validate(any(UserDto.class), eq(UserValidation.class))).thenReturn(Collections.emptySet());

        UserDto userDtoInput = new UserDto();
        userDtoInput.setFirstName(FIRST_NAME);
        userDtoInput.setLastName(LAST_NAME);
        userDtoInput.setUserName(USERNAME);
        userDtoInput.setPhone(PHONE_NUMBER);
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);
        userDtoInput.setRegisteredDate(REGISTERED_DATE);

        // Act & Assert
        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            userService.createUser(userDtoInput);
        });

        assertEquals("Email " + EMAIL + " already exists", thrown.getMessage());

        // Verify
        verify(userRepository, times(1)).findByEmail(EMAIL_ADMIN);
        verify(userRepository, times(1)).existsByEmail(EMAIL);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(validator, never()).validate(any(UserDto.class), eq(UserValidation.class));
    }

    @Test
    void testCreateUser_dataAccessException() {
        // Arrange
        setupAuthentication();
        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(validator.validate(any(UserDto.class), eq(UserValidation.class))).thenReturn(Collections.emptySet());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> userService.createUser(userDtoInput));
        assertEquals("Error saving User", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_serviceExceptionFromValidateEmail() {
        // Arrange
        setupAuthentication();
        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.createUser(userDtoInput));
        assertEquals("Email " + EMAIL + " already exists", exception.getMessage());

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_unexpectedException() {
        // Arrange
        setupAuthentication();
        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(validator.validate(any(UserDto.class), eq(UserValidation.class))).thenReturn(Collections.emptySet());
        when(passwordEncoder.encode(PASSWORD)).thenThrow(new RuntimeException("Encoding error"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.createUser(userDtoInput));
        assertEquals("Unexpected error while creating User", exception.getMessage());

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testRegisterUser_success() {
        // Arrange
        User userRegistered = new User();
        userRegistered.setId(USER_ID);
        userRegistered.setEmail(EMAIL);
        userRegistered.setPassword(ENCODED_PASSWORD);

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
    void testRegisterUser_withRole() {
        // Arrange
        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);
        userDtoInput.setRole(Role.ADMIN);

        User userToSave = new User();
        userToSave.setEmail(EMAIL);
        userToSave.setPassword(ENCODED_PASSWORD);
        userToSave.setRole(Role.ADMIN);

        User savedUser = new User();
        savedUser.setId(USER_ID);
        savedUser.setEmail(EMAIL);
        savedUser.setPassword(ENCODED_PASSWORD);
        savedUser.setRole(Role.ADMIN);

        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDto registeredUser = userService.registerUser(userDtoInput);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(EMAIL, registeredUser.getEmail());
        assertEquals(Role.ADMIN, registeredUser.getRole());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(PASSWORD);
    }

    @Test
    void testRegisterUser_repositoryException() {
        // Arrange
        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("Error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> userService.registerUser(userDtoInput));
        assertEquals("Error saving User", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(PASSWORD);
    }

    @Test
    void testRegisterUser_unexpectedException() {
        // Arrange
        UserDto userDtoInput = new UserDto();
        userDtoInput.setEmail(EMAIL);
        userDtoInput.setPassword(PASSWORD);

        when(passwordEncoder.encode(PASSWORD)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.registerUser(userDtoInput));
        assertEquals("Unexpected error while creating User", exception.getMessage());

        // Verify
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
    void testGetUserById_unauthorized() {
        // Arrange
        User authenticatedUser = new User();
        authenticatedUser.setId(2L);
        authenticatedUser.setEmail(EMAIL);
        authenticatedUser.setRole(Role.MEMBER);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(authenticatedUser));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.getUserById(USER_ID));
        assertEquals("Unauthorized: Only admins or the user themselves can get this user", exception.getMessage());

        // Verify
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void testGetUserById_dataAccessException() {
        // Arrange
        setupAuthentication();
        when(userRepository.findById(USER_ID)).thenThrow(new DataAccessException("DB error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () -> userService.getUserById(USER_ID));
        assertEquals("Error getting User with ID: " + USER_ID, exception.getMessage());

        // Verify
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testLogin_success() {
        user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setRole(Role.ADMIN);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);
        when(jwtService.createToken(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                Role.ADMIN.name()
        )).thenReturn("jwtToken");

        String token = userService.login(EMAIL, PASSWORD);

        assertNotNull(token);
        assertEquals("jwtToken", token);

        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(passwordEncoder, times(1)).matches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtService, times(1)).createToken(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                Role.ADMIN.name()
        );
    }

    @Test
    void testLogin_reencodePassword() {
        // Arrange
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setRole(Role.MEMBER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.createToken(USER_ID, FIRST_NAME, LAST_NAME, EMAIL, Role.MEMBER.name())).thenReturn("jwtToken");

        // Act
        String token = userService.login(EMAIL, PASSWORD);

        // Assert
        assertEquals("jwtToken", token);

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(PASSWORD);
    }

    @Test
    void testLogin_wrongPassword() {
        // Arrange
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", ENCODED_PASSWORD)).thenReturn(false);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.login(EMAIL, "wrongPassword"));
        assertEquals("Wrong password", exception.getMessage());

        // Verify
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAllUsers_success() {
        setupAuthentication();

        user = new User();
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<UserDto> result = userService.getAllUsers(null, null, null, mock(Pageable.class));

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAllUsers_noFilters() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mockito
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findAll(pageable);
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByFirstName(any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
        verify(userRepository, never()).findByEmail(any(), any());
    }

    @Test
    void testGetAllUsers_withFirstNameLasNameAndEmail() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mockito
        when(userRepository.findByFirstNameAndLastNameAndEmail(FIRST_NAME, LAST_NAME, EMAIL, pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(FIRST_NAME, LAST_NAME, EMAIL, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findByFirstNameAndLastNameAndEmail(FIRST_NAME,
                                                                                            LAST_NAME, EMAIL, pageable);
        verify(userRepository, never()).findByFirstName(any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
        verify(userRepository, never()).findByEmail(any(), any());
    }

    @Test
    void testGetAllUsers_withOnlyFirstName() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mock
        when(userRepository.findByFirstName(FIRST_NAME, pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(FIRST_NAME, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findByFirstName(FIRST_NAME, pageable);
        verify(userRepository, never()).findAll(pageable);
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
        verify(userRepository, never()).findByEmail(any(), any());
    }

    @Test
    void testGetAllUsers_withOnlyLastName() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mock
        when(userRepository.findByLastName(LAST_NAME, pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(null, LAST_NAME, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findByLastName(LAST_NAME, pageable);
        verify(userRepository, never()).findAll(pageable);
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByFirstName(any(), any());
        verify(userRepository, never()).findByEmail(any(), any());
    }

    @Test
    void testGetAllUsers_withOnlyEmail() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mock
        when(userRepository.findByEmail(EMAIL, pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(null, null, EMAIL, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findByEmail(EMAIL, pageable);
        verify(userRepository, never()).findAll(pageable);
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByFirstName(any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
    }

    @Test
    void testGetAllUsers_withOnlyFirstNameBlankLastNameAndEmail() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mock
        when(userRepository.findByFirstName(FIRST_NAME, pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(FIRST_NAME, "", "", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findByFirstName(FIRST_NAME, pageable);
        verify(userRepository, never()).findAll(pageable);
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
        verify(userRepository, never()).findByEmail(any(), any());
    }

    @Test
    void testGetAllUsers_withOnlyFirstNameLastNameBlankEmail() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mock
        when(userRepository.findByFirstName(FIRST_NAME, pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(FIRST_NAME, LAST_NAME, "", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findByFirstName(FIRST_NAME, pageable);
        verify(userRepository, never()).findAll(pageable);
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
        verify(userRepository, never()).findByEmail(any(), any());
    }

    @Test
    void testGetAllUsers_withOnlyFirstNameLastNameNullEmail() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        // Mock
        when(userRepository.findByFirstName(FIRST_NAME, pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = userService.getAllUsers(FIRST_NAME, LAST_NAME, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        //noinspection SequencedCollectionMethodCanBeUsed
        assertEquals(user.getId(), result.getContent().get(0).getId());

        // Verify
        verify(userRepository, times(1)).findByFirstName(FIRST_NAME, pageable);
        verify(userRepository, never()).findAll(pageable);
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
        verify(userRepository, never()).findByEmail(any(), any());
    }

    @Test
    void getAllUsers_repositoryException() {
        // Arrange
        setupAuthentication();
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByEmail(EMAIL, pageable)).thenThrow(new DataAccessException("Error") {});

        // Act & Assert
        RepositoryException exception = assertThrows(RepositoryException.class, () ->
                userService.getAllUsers(null, null, EMAIL, pageable));
        assertEquals("Error getting Users from repository", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).findByEmail(EMAIL, pageable);
        verify(userRepository, never()).findAll(any(Pageable.class));
        verify(userRepository, never()).findByFirstNameAndLastNameAndEmail(any(), any(), any(), any());
        verify(userRepository, never()).findByFirstName(any(), any());
        verify(userRepository, never()).findByLastName(any(), any());
    }

    @Test
    void testUpdateUser_success() {
        // Arrange
        setupAuthentication();

        User existingUser = new User();
        existingUser.setId(USER_ID);
        existingUser.setEmail(EMAIL);
        existingUser.setRole(Role.MEMBER);

        UserDto userDtoInput = new UserDto();
        userDtoInput.setId(USER_ID);
        userDtoInput.setFirstName("UpdatedFirstName");
        userDtoInput.setLastName("UpdatedLastName");
        userDtoInput.setUserName("UpdatedUserName");
        userDtoInput.setPhone("UpdatedPhone");
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
        setupAuthentication();

        when(userRepository.existsById(USER_ID)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.deleteUser(USER_ID));
        assertEquals("User with id " + USER_ID + " not found", exception.getMessage());
    }

    private void setupAuthentication() {
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
    }
}
