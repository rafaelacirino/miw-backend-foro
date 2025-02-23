package es.upm.miw.foro.persistence.model;

import es.upm.miw.foro.persistance.model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleTest {

    @Test
    void testRoleOf() {
        assertEquals(Role.ADMIN, Role.of("ROLE_ADMIN"));
        assertEquals(Role.MANAGER, Role.of("ROLE_MANAGER"));
        assertEquals(Role.OPERATOR, Role.of("ROLE_OPERATOR"));
        assertEquals(Role.CUSTOMER, Role.of("ROLE_CUSTOMER"));
        assertEquals(Role.AUTHENTICATED, Role.of("ROLE_AUTHENTICATED"));
    }

    @Test
    void testRoleOfWithInvalidRole() {
        assertThrows(IllegalArgumentException.class, () -> Role.of("ROLE_INVALID"));
    }
}
