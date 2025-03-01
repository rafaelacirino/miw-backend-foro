package es.upm.miw.foro.api.dto.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PasswordValidatorTest {

    private PasswordValidator passwordValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void testValidPasswords() {
        assertThat(passwordValidator.isValid("Valid1@pass", context)).isTrue();
        assertThat(passwordValidator.isValid("Str0ng!Pass", context)).isTrue();
        assertThat(passwordValidator.isValid("Aa1@bb2Cc", context)).isTrue();
    }

    @Test
    void testInvalidPasswords() {
        assertThat(passwordValidator.isValid(null, context)).isFalse(); // Null password
        assertThat(passwordValidator.isValid("", context)).isFalse(); // Empty password
        assertThat(passwordValidator.isValid("short1@", context)).isFalse(); // Too short
        assertThat(passwordValidator.isValid("NOLOWERCASE1@", context)).isFalse(); // No lowercase
        assertThat(passwordValidator.isValid("nouppercase1@", context)).isFalse(); // No uppercase
        assertThat(passwordValidator.isValid("NoNumber@!", context)).isFalse(); // No number
        assertThat(passwordValidator.isValid("NoSpecialChar1", context)).isFalse(); // No special character
        assertThat(passwordValidator.isValid("WayTooLongPassword123@", context)).isFalse(); // Too long
    }
}
