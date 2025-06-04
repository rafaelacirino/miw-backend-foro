package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>, JpaSpecificationExecutor<Answer> {

    List<Answer> findByQuestionOrderByCreationDateAsc(Question question);

    @Query("SELECT a FROM Answer a JOIN FETCH a.author WHERE a.id = :id")
    Optional<Answer> findByIdWithAuthor(@Param("id") Long id);
}
