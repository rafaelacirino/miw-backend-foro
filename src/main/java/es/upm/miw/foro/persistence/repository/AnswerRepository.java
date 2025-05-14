package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Answer;
import es.upm.miw.foro.persistence.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionOrderByCreationDateAsc(Question question);
}
