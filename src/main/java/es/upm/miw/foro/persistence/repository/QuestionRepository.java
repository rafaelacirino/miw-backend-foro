package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    boolean existsByAuthorId(Long userId);

    @EntityGraph(attributePaths = {"author", "answers.author", "tags"})
    @Query("""
    SELECT DISTINCT q FROM Question q
    LEFT JOIN q.answers a
    WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(q.description) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(a.content) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Question> searchByTitleOrDescriptionOrAnswerContentContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdWithAuthor(@Param("id") Long id);

    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdWithAuthorAndTags(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Question q SET q.views = q.views + 1 WHERE q.id = :id")
    void incrementViews(@Param("id") Long id);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.tags LEFT JOIN FETCH q.answers WHERE q.id = :id")
    Optional<Question> findByIdWithDetails(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Question q SET q.author.id = :newAuthorId WHERE q.author.id = :oldAuthorId")
    void updateAuthorId(@Param("oldAuthorId") Long oldAuthorId, @Param("newAuthorId") Long newAuthorId);
}
