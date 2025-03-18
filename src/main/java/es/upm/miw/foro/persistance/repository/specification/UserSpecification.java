package es.upm.miw.foro.persistance.repository.specification;

import es.upm.miw.foro.api.dto.UserDto;
import es.upm.miw.foro.persistance.model.User;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> buildUserSpecification(UserDto userDto) {
        return (root, query, criteriabuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addPredicate(predicates, userDto.getId(), value -> criteriabuilder.equal(root.get("id"), value));
            addPredicate(predicates, userDto.getFirstName(), value -> criteriabuilder.equal(root.get("firstName"), value));
            addPredicate(predicates, userDto.getLastName(), value -> criteriabuilder.equal(root.get("lastName"), value));
            addPredicate(predicates, userDto.getEmail(), value -> criteriabuilder.equal(root.get("email"), value));
            addPredicate(predicates, userDto.getPassword(), value -> criteriabuilder.equal(root.get("password"), value));
            addPredicate(predicates, userDto.getRole(), value -> criteriabuilder.equal(root.get("role"), value));
            addPredicate(predicates, userDto.getRegisteredDate(), value -> criteriabuilder.equal(root.get("registeredDate"), value));

            return criteriabuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static <T> void addPredicate(List<Predicate> predicates, T value, Function<T, Predicate> predicateMapper) {
        if (value instanceof String string) {
            if (StringUtils.isNotBlank(string)) {
                predicates.add(predicateMapper.apply(value));
            }
        } else if (value != null) {
            predicates.add(predicateMapper.apply(value));
        }
    }
}
