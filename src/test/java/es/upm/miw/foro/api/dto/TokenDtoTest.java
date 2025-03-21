package es.upm.miw.foro.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDtoTest {

    @Test
    void testNoArgsConstructor() {
        TokenDto tokenDto = new TokenDto();
        assertThat(tokenDto).isNotNull();
        assertThat(tokenDto.getToken()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        TokenDto tokenDto = new TokenDto("sampleToken123");
        assertThat(tokenDto.getToken()).isEqualTo("sampleToken123");
    }

    @Test
    void testSettersAndGetters() {
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken("newToken456");

        assertThat(tokenDto.getToken()).isEqualTo("newToken456");
    }

    @Test
    void testToString() {
        TokenDto tokenDto = new TokenDto("sampleToken123");
        String expectedToString = "TokenDto(token=sampleToken123)";
        assertThat(tokenDto.toString()).hasToString(expectedToString);
    }
}
