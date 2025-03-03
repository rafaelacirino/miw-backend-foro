package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final LocalDateTime REGISTRED_DATE = LocalDateTime.now();

    @Test
    void testConstructorThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, UserMapper::new);
    }

    @Test
    void toUserDto_shouldMapEntityToDto() {
        // Arrange
        User user = createUserEntity();

        // Act
        UserDto userDto = UserMapper.toUserDto(user);

        // Assert
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.getRole(), userDto.getRole());
        assertEquals(user.getRegisteredDate(), userDto.getRegisteredDate());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Arrange
        UserDto userDto = createUserDto();

        // Act
        User user = UserMapper.toEntity(userDto);

        // Assert
        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.getRole(), user.getRole());
        assertEquals(userDto.getRegisteredDate(), user.getRegisteredDate());
    }

    @Test
    void toDtoList_shouldMapEntityListToDtoList() {
        // Arrange
        List<User> userList = Arrays.asList(createUserEntity(), createUserEntity());

        // Act
        List<UserDto> dtoList = UserMapper.toDtoList(userList);

        // Assert
        assertNotNull(dtoList);
        assertEquals(userList.size(), dtoList.size());
    }

    @Test
    void toEntityList_shouldMapDtoListToEntityList() {
        // Arrange
        List<UserDto> dtoList = Arrays.asList(createUserDto(), createUserDto());

        // Act
        List<User> entityList = UserMapper.toEntityList(dtoList);

        // Assert
        assertNotNull(entityList);
        assertEquals(dtoList.size(), entityList.size());
    }

    @Test
    void toDto_shouldReturnNullWhenEntityIsNull() {
        // Act
        UserDto dto = UserMapper.toUserDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        // Act
        User entity = UserMapper.toEntity(null);

        // Assert
        assertNull(entity);
    }

    private User createUserEntity() {
        User user = new User();
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(Role.ADMIN);
        user.setRegisteredDate(REGISTRED_DATE);

        return user;
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRole(Role.ADMIN);
        userDto.setRegisteredDate(REGISTRED_DATE);

        return userDto;
    }

}
