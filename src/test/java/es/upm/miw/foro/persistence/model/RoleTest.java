package es.upm.miw.foro.persistence.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleTest {

    @Test
    void testRoleOf() {
        assertEquals(Role.ADMIN, Role.of("ROLE_ADMIN"));
        assertEquals(Role.MEMBER, Role.of("ROLE_MEMBER"));
    }

    @Test
    void testRoleOfWithInvalidRole() {
        assertThrows(IllegalArgumentException.class, () -> Role.of("ROLE_INVALID"));
    }
}
