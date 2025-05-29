package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    @EntityGraph(attributePaths = {"author", "answers.author", "tags"})
    @Query("""
    SELECT DISTINCT q FROM Question q
    LEFT JOIN q.answers a
    WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(q.description) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(a.content) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Question> searchByTitleOrDescriptionOrAnswerContentContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Question q SET q.views = q.views + 1 WHERE q.id = :id")
    void incrementViews(@Param("id") Long id);
}
