package es.upm.miw.foro.persistence.repository.specification;

import es.upm.miw.foro.persistence.model.Answer;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerSpecificationTest {

    @Mock
    private Root<Answer> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> path;

    @Mock
    private Path<Object> nestedPath;

    @Mock
    private Predicate predicate;

    private static final String EMAIL = "test@example.com";
    private static final String QUESTION = "Question";
    private static final String CONTENT = "Sample content";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buildAnswerSpecification_AllFieldsProvided() {
        // Given
        when(root.get("author")).thenReturn(path);
        when(path.get("email")).thenReturn(nestedPath);
        when(root.get("question")).thenReturn(path);
        when(root.get("content")).thenReturn(path);
        when(root.get("creationDate")).thenReturn(path);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);

        // When
        Specification<Answer> specification = AnswerSpecification.buildAnswerSpecification(
                EMAIL, QUESTION, CONTENT, CREATION_DATE);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }

    @Test
    void buildAnswerSpecification_SomeFieldsNull() {
        // Given
        when(root.get("author")).thenReturn(path);
        when(path.get("email")).thenReturn(nestedPath);
        when(root.get("content")).thenReturn(mock(Path.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);

        // When
        Specification<Answer> specification = AnswerSpecification.buildAnswerSpecification(
                EMAIL, null, CONTENT, null);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }

    @Test
    void buildAnswerSpecification_AllFieldsNull() {
        // When
        Specification<Answer> specification = AnswerSpecification.buildAnswerSpecification(
                null, null, null, null);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }
}

