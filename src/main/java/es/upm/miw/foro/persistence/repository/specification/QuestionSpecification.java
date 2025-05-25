package es.upm.miw.foro.persistence.repository.specification;

import es.upm.miw.foro.persistence.model.Question;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class QuestionSpecification {

    private QuestionSpecification() {
    }

    public static Specification<Question> buildQuestionSpecification(String email, String title, LocalDateTime fromDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addPredicate(predicates, email, value -> cb.equal(root.get("author").get("email"), value));
            addPredicate(predicates, title, value -> cb.like(cb.lower(root.get("title")), "%" + value.toLowerCase() + "%"));
            addPredicate(predicates, fromDate, value -> cb.greaterThanOrEqualTo(root.get("creationDate"), value));

            return cb.and(predicates.toArray(new Predicate[0]));
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
