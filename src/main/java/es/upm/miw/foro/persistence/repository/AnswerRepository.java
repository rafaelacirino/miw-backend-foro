package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>, JpaSpecificationExecutor<Answer> {

    boolean existsByAuthorId(Long userId);

    List<Answer> findByQuestionOrderByCreationDateAsc(Question question);

    @Query("SELECT a FROM Answer a JOIN FETCH a.author WHERE a.id = :id")
    Optional<Answer> findByIdWithAuthor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Answer a SET a.author.id = :newAuthorId WHERE a.author.id = :oldAuthorId")
    void updateAuthorId(@Param("oldAuthorId") Long oldAuthorId, @Param("newAuthorId") Long newAuthorId);
}
