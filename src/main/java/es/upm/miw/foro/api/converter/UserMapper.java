package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.persistence.model.User;

import java.util.List;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        populateDto(user, userDto);
        return userDto;
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User();
        populateEntity(user, userDto);
        return user;
    }

    public static List<UserDto> toDtoList(List<User> userList) {
        return userList.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public static List<User> toEntityList(List<UserDto> userDtoList) {
        return userDtoList.stream()
                .map(UserMapper::toEntity)
                .toList();
    }

    private static void populateDto(User user, UserDto userDto) {
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUserName(user.getUserName());
        userDto.setPhone(user.getPhone());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole());
        userDto.setRegisteredDate(user.getRegisteredDate());
    }

    private static void populateEntity(User entity, UserDto userDto) {
        entity.setId(userDto.getId());
        entity.setFirstName(userDto.getFirstName());
        entity.setLastName(userDto.getLastName());
        entity.setUserName(userDto.getUserName());
        entity.setPhone(userDto.getPhone());
        entity.setEmail(userDto.getEmail());
        entity.setPassword(userDto.getPassword());
        entity.setRole(userDto.getRole());
        entity.setRegisteredDate(userDto.getRegisteredDate());
    }
}
