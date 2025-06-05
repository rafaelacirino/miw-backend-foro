package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.UserMapper;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.api.dto.validation.UserValidation;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistence.model.Role;
import es.upm.miw.foro.persistence.model.User;
import es.upm.miw.foro.persistence.repository.UserRepository;
import es.upm.miw.foro.service.UserService;
import es.upm.miw.foro.util.MessageUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtServiceImpl jwtServiceImpl;
    private final Validator validator;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtServiceImpl jwtServiceImpl, Validator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtServiceImpl = jwtServiceImpl;
        this.validator = validator;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User currentUser = getAuthenticatedUserWithRole();
        log.info("Current user: {} with role: {}", currentUser.getFirstName(), currentUser.getRole());
        return saveUser(userDto);
    }

    @Override
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        return saveUser(userDto);
    }

    private UserDto saveUser(UserDto userDto) {
        try {
            validateEmail(userDto.getEmail());
            validateUserName(userDto.getUserName());
            validateUserDto(userDto);
            if (userDto.getRole() == null) {
                userDto.setRole(Role.MEMBER);
            }
            User user = UserMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User savedUser = userRepository.save(user);
            return UserMapper.toUserDto(savedUser);
        } catch (ServiceException e) {
            throw e;
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error saving User", exception);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error saving user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDto getUserById(Long id) {
        try {
            User currentUser = getAuthenticatedUser();
            if (!currentUser.getId().equals(id) && !Role.ADMIN.equals(currentUser.getRole())) {
                throw new ServiceException("Unauthorized: Only admins or the user themselves can get this user");
            }
            return userRepository.findById(id)
                    .map(UserMapper::toUserDto)
                    .orElseThrow(() -> new ServiceException("User with ID " + id + MessageUtil.NOT_FOUND));
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error getting User with ID: " + id, exception);
        }
    }

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException("User with email " + email + MessageUtil.NOT_FOUND, HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            if (user.getPassword().equals(password)) {
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
                userRepository.save(user);
            } else {
                throw new ServiceException("Incorrect password", HttpStatus.UNAUTHORIZED);
            }
        }
        return jwtServiceImpl.createToken(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public Page<UserDto> getAllUsers(String firstName, String lastName, String email, Pageable pageable) {
        try {
            User currentUser = getAuthenticatedUserWithRole();
            log.info("Authenticated user: {} with role: {}", currentUser.getFirstName(), currentUser.getRole());
            if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()
                                                                            && email != null && !email.isBlank()) {
                return userRepository.findByFirstNameAndLastNameAndEmail(firstName, lastName, email, pageable)
                        .map(UserMapper::toUserDto);
            } else if (firstName != null) {
                return userRepository.findByFirstName(firstName, pageable)
                        .map(UserMapper::toUserDto);
            } else if (lastName != null) {
                return userRepository.findByLastName(lastName, pageable)
                        .map(UserMapper::toUserDto);
            } else if(email != null) {
                return userRepository.findByEmail(email, pageable)
                        .map(UserMapper::toUserDto);
            } else {
                return userRepository.findAll(pageable)
                        .map(UserMapper::toUserDto);
            }
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error getting Users from repository", exception);
        }
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        try {
            User currentUser = getAuthenticatedUser();
            if (!currentUser.getId().equals(id) && !Role.ADMIN.equals(currentUser.getRole())) {
                throw new ServiceException("Unauthorized: Only admins or the user themselves can update this user", HttpStatus.FORBIDDEN);
            }
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("User with id " + id + MessageUtil.NOT_FOUND, HttpStatus.NOT_FOUND));

            if (!existingUser.getEmail().equals(userDto.getEmail())) {
                validateEmail(userDto.getEmail());
            }
            if (!existingUser.getUserName().equals(userDto.getUserName())) {
                validateUserName(userDto.getUserName());
            }
            validateUserDto(userDto);

            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setUserName(userDto.getUserName());
            existingUser.setPhone(userDto.getPhone());
            existingUser.setEmail(userDto.getEmail());

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            User updatedUser = this.userRepository.save(existingUser);
            return UserMapper.toUserDto(updatedUser);

        } catch (ServiceException e) {
            throw e;
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error updating User", exception);
        } catch (Exception exception) {
            throw new ServiceException("Unexpected error while updating User", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        try {
            User currentUser = getAuthenticatedUser();
            if (!currentUser.getId().equals(id) && !Role.ADMIN.equals(currentUser.getRole())) {
                throw new ServiceException("Unauthorized: Only admins or the user themselves can delete this user", HttpStatus.UNAUTHORIZED);
            }
            if (!userRepository.existsById(id)) {
                throw new ServiceException("User with id " + id + MessageUtil.NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            userRepository.deleteById(id);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error deleting user with id " + id, exception);
        }
    }

    @Override
    public boolean verifyPassword(Long userId, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found"));
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("Authentication is null");
            throw new ServiceException("Unauthorized: No user is logged in");
        }
        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ServiceException("Authenticated user not found", HttpStatus.NOT_FOUND));
    }

    private User getAuthenticatedUserWithRole() {
        User currentUser = getAuthenticatedUser();
        if (!Role.ADMIN.equals(currentUser.getRole())) {
            log.warn("User {} does not have required role {}", currentUser.getEmail(), Role.ADMIN);
            throw new ServiceException("Unauthorized: User does not have the required role: " + Role.ADMIN);
        }
        return currentUser;
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.error("Email already exists: {}", email);
            throw new ServiceException("Email already exists", HttpStatus.CONFLICT);
        }
    }

    private void validateUserName(String userName) {
        if (userRepository.existsByUserName(userName)) {
            log.error("Username already exists: {}", userName);
            throw new ServiceException("Username already exists", HttpStatus.CONFLICT);
        }
    }

    private void validateUserDto(UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, UserValidation.class);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<UserDto> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();

                if ("email".equals(field) || "password".equals(field)) {
                    throw new ServiceException(message, HttpStatus.BAD_REQUEST);
                }
            }
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Validation error");
            throw new ServiceException(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
