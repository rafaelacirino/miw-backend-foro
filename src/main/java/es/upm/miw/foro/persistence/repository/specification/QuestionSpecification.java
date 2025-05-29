package es.upm.miw.foro.persistence.repository.specification;

import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.Tag;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class QuestionSpecification {

    private QuestionSpecification() {}

    public static Specification<Question> buildQuestionSpecification(String email, String title, LocalDateTime fromDate, Boolean unanswered, String tag) {
        return (root, query, criteriabuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addPredicate(predicates, email, e -> criteriabuilder.equal(root.get("author").get("email"), e));
            addPredicate(predicates, title, t -> criteriabuilder.like(criteriabuilder.lower(root.get("title")), "%" + t.toLowerCase() + "%"));
            addPredicate(predicates, fromDate, date -> criteriabuilder.greaterThanOrEqualTo(root.get("creationDate"), date));
            addPredicate(predicates, unanswered, u -> criteriabuilder.isEmpty(root.get("answers")));

            if (StringUtils.isNotBlank(tag)) {
                Join<Question, Tag> tags = root.join("tags", JoinType.INNER);
                predicates.add(criteriabuilder.equal(criteriabuilder.lower(tags.get("name")), tag.toLowerCase()));
                if (query != null) {
                    query.distinct(true);
                }
            }

            return criteriabuilder.and(predicates.toArray(new Predicate[0]));
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
