package es.upm.miw.foro.persistance.repository;

import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.repository.specification.QuestionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    Page<Question> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("""
    SELECT q FROM Question q
    WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(q.description) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Question> searchByTitleOrDescriptionContainingIgnoreCase(
            @Param("query") String query, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"author", "answers"})
    Optional<Question> findById(UUID id);

    default Page<Question> findMyQuestions(String email, String title, LocalDate fromDate, Pageable pageable) {
        return findAll(
                Specification.where(QuestionSpecification.findByAuthorEmail(email))
                        .and(QuestionSpecification.titleContains(title))
                        .and(QuestionSpecification.createdAfter(fromDate)),
                pageable
        );
    }

    Page<Question> findAll(Specification<Question> and, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Question q SET q.views = q.views + 1 WHERE q.id = :id")
    void incrementViews(@Param("id") UUID id);
}
