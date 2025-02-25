package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.persistance.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        populateDto(user, userDto);
        return userDto;
    }

    public static User toUser(UserDto userDto) {
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
                .collect(Collectors.toList());
    }

    // Replace this usage of 'Stream. collect(Collectors. toList())'
    // with 'Stream. toList()' and ensure that the list is unmodified.
    public static List<User> toEntityList(List<UserDto> userDtoList) {
        return userDtoList.stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());
    }

    private static void populateDto(User user, UserDto userDto) {
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        userDto.setRole(user.getRole());
        userDto.setRegistredDate(user.getRegistredDate());
    }

    private static void populateEntity(User entity, UserDto userDto) {
        entity.setId(userDto.getId());
        entity.setFirstName(userDto.getFirstName());
        entity.setLastName(userDto.getLastName());
        entity.setEmail(userDto.getEmail());
        entity.setPassword(userDto.getPassword());
        entity.setPhone(userDto.getPhone());
        entity.setAddress(userDto.getAddress());
        entity.setRole(userDto.getRole());
        entity.setRegistredDate(userDto.getRegistredDate());
    }

}
