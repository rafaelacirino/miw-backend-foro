package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.persistance.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto registerUser(UserDto userDto);

    UserDto getUserById(UUID id);

    UserDto getUserByEmail(String email);

    String  login(String email, String password);

    Page<UserDto> getAllUsers(String firstName, String lastName, String email, Pageable pageable);

    UserDto updateUser(UUID id, UserDto userDto);

    void deleteUser(UUID id);

    User getAuthenticatedUser();
}
