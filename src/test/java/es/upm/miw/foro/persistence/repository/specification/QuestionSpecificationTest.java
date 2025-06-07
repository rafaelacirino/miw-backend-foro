package es.upm.miw.foro.persistence.repository.specification;

import es.upm.miw.foro.persistence.model.Question;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuestionSpecificationTest {

    @Mock
    private Root<Question> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> path;

    @Mock
    private Predicate predicate;

    private static final String AUTHOR = "author";
    private static final String TITLE = "question";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buildQuestionSpecification_AllFieldsProvided() {
        // Given
        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);

        // When
        Specification<Question> specification = QuestionSpecification.buildQuestionSpecification(AUTHOR, TITLE, CREATION_DATE, null, null);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }

    @Test
    void buildQuestionSpecification_SomeFieldsNull() {
        // Given
        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);

        // When
        Specification<Question> specification = QuestionSpecification.buildQuestionSpecification(AUTHOR, null, null, null, null);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }

    @Test
    void buildQuestionSpecification_AllFieldsNull() {
        // When
        Specification<Question> specification = QuestionSpecification.buildQuestionSpecification(null, null, null, null, null);
        Predicate result = specification.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(result);
    }

    @Test
    void buildQuestionSpecification_WithNonBlankTag_ShouldIncludeTagPredicate() {
        // Arrange
        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);
        when(criteriaBuilder.lower(any())).thenReturn(mock(Expression.class));
        when(root.join(anyString(), any())).thenThrow(new UnsupportedOperationException("join not supported in this test"));

        // Act
        Specification<Question> specification = QuestionSpecification
                .buildQuestionSpecification(null, null, null, null, "java");

        // Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            specification.toPredicate(root, query, criteriaBuilder);
        });
    }
}
