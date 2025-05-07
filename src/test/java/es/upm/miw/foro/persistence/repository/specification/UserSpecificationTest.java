package es.upm.miw.foro.persistence.repository.specification;

import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.persistance.model.Role;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.specification.UserSpecification;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserSpecificationTest {

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> path;

    @Mock
    private Predicate predicate;

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "UserName";
    private static final String LAST_NAME = "UserLastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final LocalDateTime REGISTERED_DATE = LocalDateTime.now();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buildUserSpecification_AllFieldsProvided() {
        // Given
        UserDto dto = new UserDto();
        dto.setId(USER_ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setRole(Role.MEMBER);
        dto.setRegisteredDate(REGISTERED_DATE);

        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);

        // When
        Specification<User> specification = UserSpecification.buildUserSpecification(dto);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }

    @Test
    void buildUserSpecification_SomeFieldsNull() {
        // Given
        UserDto dto = new UserDto();
        dto.setId(USER_ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(null);
        dto.setEmail(null);
        dto.setPassword(null);
        dto.setRole(null);
        dto.setRegisteredDate(null);

        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);

        // When
        Specification<User> specification = UserSpecification.buildUserSpecification(dto);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }

    @Test
    void buildUserSpecification_AllFieldsNull() {
        // Given
        UserDto dto = new UserDto();

        // When
        Specification<User> specification = UserSpecification.buildUserSpecification(dto);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }
}
