package es.upm.miw.foro.service.impl;

import es.upm.miw.foro.api.converter.UserMapper;
import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.exception.RepositoryException;
import es.upm.miw.foro.exception.ServiceException;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;
import es.upm.miw.foro.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
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
        // get the user authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ServiceException("Authenticated user not found"));
        if (!Role.ADMIN.equals(currentUser.getRole())) {
            throw new ServiceException("Unauthorized: Only admins can create users");
        }
        try {
            User user = UserMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User savedUser = this.userRepository.save(user);
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
                userDto.setRole(Role.CUSTOMER);
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
            return userRepository.findById(id)
                    .map(UserMapper::toUserDto)
                    .orElseThrow(() -> new ServiceException("User with id " + id + " not found"));
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error getting User with id " + id, exception);
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
            if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank() && email != null && !email.isBlank()) {
                return userRepository.findByFirstNameAndLastNameAndEmail(firstName, lastName, email, pageable)
                        .map(UserMapper::toUserDto);
            } else if (firstName != null) {
                return userRepository.findByFirstName(firstName, pageable)
                        .map(UserMapper::toUserDto);
            } else if (lastName != null) {
                return userRepository.findByLastName(lastName, pageable)
                        .map(UserMapper::toUserDto);
            } else if (email != null) {
                return userRepository.findByEmail(email)
                        .map(user -> (Page<UserDto>) new PageImpl<>(List.of(UserMapper.toUserDto(user)), pageable, 1))
                        .orElseGet(() -> Page.empty(pageable));
            } else {
                return userRepository.findAll(pageable)
                        .map(UserMapper::toUserDto);
            }
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error getting Users", exception);
        }
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("User with id " + id + " not found"));

            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setPassword(userDto.getPassword());
            existingUser.setPhone(userDto.getPhone());
            existingUser.setAddress(userDto.getAddress());
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
            if (!userRepository.existsById(id)) {
                throw new ServiceException("User with id " + id + " not found");
            }
            userRepository.deleteById(id);
        } catch (DataAccessException exception) {
            throw new RepositoryException("Error deleting User with id " + id, exception);
        }
    }
}
