package es.upm.miw.foro.service;

import es.upm.miw.foro.api.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(String firstName, String lastName, String email, Pageable pageable);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
