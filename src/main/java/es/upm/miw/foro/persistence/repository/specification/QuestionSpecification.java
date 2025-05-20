package es.upm.miw.foro.persistence.repository.specification;

import es.upm.miw.foro.persistence.model.Question;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class QuestionSpecification {

    public static Specification<Question> findByAuthorEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("author").get("email"), email);
    }

    public static Specification<Question> titleContains(String title) {
        return (root, query, cb) ->
                title == null ?
                        cb.conjunction() :
                        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Question> createdAfter(LocalDateTime fromDate) {
        return (root, query, cb) ->
                fromDate == null ?
                        cb.conjunction() :
                        cb.greaterThanOrEqualTo(root.get("creationDate"), fromDate);
    }
}
