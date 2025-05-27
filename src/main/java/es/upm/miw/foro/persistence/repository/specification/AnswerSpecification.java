package es.upm.miw.foro.persistence.repository.specification;

import es.upm.miw.foro.persistence.model.Answer;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AnswerSpecification {

    private AnswerSpecification() {
    }

    public static Specification<Answer> buildAnswerSpecification(String email, String question, String content, LocalDateTime creationDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addPredicate(predicates, email, value -> criteriaBuilder.equal(root.get("author").get("email"), value));
            addPredicate(predicates, question, value -> criteriaBuilder.equal(root.get("question"), value));
            addPredicate(predicates, content, value -> criteriaBuilder.equal(root.get("content"), value));
            addPredicate(predicates, creationDate, value -> criteriaBuilder.equal(root.get("creationDate"), value));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> void addPredicate(List<Predicate> predicates, T value, Function<T, Predicate> predicateMapper) {
        if (value instanceof String string) {
            if (StringUtils.isNotBlank(string)) {
                predicates.add(predicateMapper.apply(value));
            }
        } else if (value != null) {
            predicates.add(predicateMapper.apply(value));
        }
    }
}
