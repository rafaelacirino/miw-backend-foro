package es.upm.miw.foro.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginDtoTest {

    @Test
    void testNoArgsConstructor() {
        LoginDto loginDto = new LoginDto();
        assertThat(loginDto).isNotNull();
        assertThat(loginDto.getEmail()).isNull();
        assertThat(loginDto.getPassword()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        LoginDto loginDto = new LoginDto("test@example.com", "password123");
        assertThat(loginDto.getEmail()).isEqualTo("test@example.com");
        assertThat(loginDto.getPassword()).isEqualTo("password123");
    }

    @Test
    void testSettersAndGetters() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("securePass");

        assertThat(loginDto.getEmail()).isEqualTo("user@example.com");
        assertThat(loginDto.getPassword()).isEqualTo("securePass");
    }

    @Test
    void testToString() {
        LoginDto loginDto = new LoginDto("test@example.com", "password123");
        String expectedToString = "LoginDto(email=test@example.com, password=password123)";
        assertThat(loginDto.toString()).isEqualTo(expectedToString);
    }
}
