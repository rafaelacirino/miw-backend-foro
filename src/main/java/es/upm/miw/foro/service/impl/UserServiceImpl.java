package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.UserMapper;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import es.upm.miw.foro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User currentUser = getAuthenticatedUserWithRole(Role.ADMIN);
        log.info("Current user: {} with role: {}", currentUser.getFirstName(), currentUser.getRole());
        try {
            if (userDto.getRole() == null) {
                userDto.setRole(Role.MEMBER);
            }
            User user = UserMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User savedUser = userRepository.save(user);
            return UserMapper.toUserDto(savedUser);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error saving User", exception);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while creating User", e);
        }
    }

    @Override
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        try {
            if (userDto.getRole() == null) {
                userDto.setRole(Role.MEMBER);
            }
            User user = UserMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User savedUser = userRepository.save(user);
            return UserMapper.toUserDto(savedUser);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error saving User", exception);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error while creating User", e);
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
                    .orElseThrow(() -> new ServiceException("User with ID " + id + " not found"));
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error getting User with ID: " + id, exception);
        }
    }

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ServiceException("User with email " + email + " not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            if (user.getPassword().equals(password)) {
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
                userRepository.save(user);
            } else {
                throw new ServiceException("Wrong password");
            }
        }
        return jwtService.createToken(user.getEmail(), user.getFirstName(), user.getRole().name());
    }

    @Override
    public Page<UserDto> getAllUsers(String firstName, String lastName, String email, Pageable pageable) {
        try {
            User currentUser = getAuthenticatedUserWithRole(Role.ADMIN);
            log.info("Authenticated user: {} with role: {}", currentUser.getFirstName(), currentUser.getRole());
            if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty() && email != null && !email.isEmpty()) {
                return userRepository.findByFirstNameAndLastNameAndEmail(firstName, lastName, email, pageable)
                        .map(UserMapper::toUserDto);
            } else if(firstName != null){
                return userRepository.findByFirstName(firstName, pageable)
                        .map(UserMapper::toUserDto);
            } else if(lastName != null){
                return userRepository.findByLastName(lastName, pageable)
                        .map(UserMapper::toUserDto);
            } else if(email != null){
                return userRepository.findByEmail(email, pageable)
                        .map(UserMapper::toUserDto);
            } else {
                return userRepository.findAll(pageable)
                        .map(UserMapper::toUserDto);
            }
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error retrieving Users from repository", exception);
        }
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        try {
            User currentUser = getAuthenticatedUser();
            if (!currentUser.getId().equals(id) && !Role.ADMIN.equals(currentUser.getRole())) {
                throw new ServiceException("Unauthorized: Only admins or the user themselves can update this user");
            }
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("User with id " + id + " not found"));

            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setPassword(userDto.getPassword());
            existingUser.setRole(userDto.getRole());
            existingUser.setRegisteredDate(userDto.getRegisteredDate());

            User updatedUser = this.userRepository.save(existingUser);
            return UserMapper.toUserDto(updatedUser);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error updating User with id " + id, exception);
        } catch (Exception exception) {
            throw new ServiceException("Unexpected error while updating User with id " + id, exception);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        try {
            User currentUser = getAuthenticatedUser();
            if (!currentUser.getId().equals(id) && !Role.ADMIN.equals(currentUser.getRole())) {
                throw new ServiceException("Unauthorized: Only admins or the user themselves can delete this user");
            }
            if (!userRepository.existsById(id)) {
                throw new ServiceException("User with id " + id + " not found");
            }
            userRepository.deleteById(id);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error deleting User with id " + id, exception);
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("Authentication is null");
            throw new ServiceException("Unauthorized: No user is logged in");
        }
        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ServiceException("Authenticated user not found"));
    }

    private User getAuthenticatedUserWithRole(Role requiredRole) {
        User currentUser = getAuthenticatedUser();
        if (!requiredRole.equals(currentUser.getRole())) {
            log.warn("User {} does not have required role {}", currentUser.getEmail(), requiredRole);
            throw new ServiceException("Unauthorized: User does not have the required role: " + requiredRole);
        }
        return currentUser;
    }
}
