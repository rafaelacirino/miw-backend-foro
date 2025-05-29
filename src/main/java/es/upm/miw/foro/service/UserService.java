package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto registerUser(UserDto userDto);

    UserDto getUserById(Long id);

    String  login(String email, String password);

    Page<UserDto> getAllUsers(String firstName, String lastName, String email, Pageable pageable);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    User getAuthenticatedUser();
}
