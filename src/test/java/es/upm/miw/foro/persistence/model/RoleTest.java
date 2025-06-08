package es.upm.miw.foro.persistence.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleTest {

    @Test
    void testRoleValuesContainsAllRoles() {
        Role[] roles = Role.values();
        assertThat(roles).containsExactly(Role.ADMIN, Role.MEMBER, Role.UNKNOWN);
    }

    @Test
    void testValueOfReturnsCorrectEnum() {
        assertThat(Role.valueOf("ADMIN")).isEqualTo(Role.ADMIN);
        assertThat(Role.valueOf("MEMBER")).isEqualTo(Role.MEMBER);
        assertThat(Role.valueOf("UNKNOWN")).isEqualTo(Role.UNKNOWN);
    }

    @Test
    void testValueOfThrowsExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("INVALID"));
    }

}
