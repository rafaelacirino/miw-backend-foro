package es.upm.miw.foro.persistance.repository;

import es.upm.miw.foro.persistance.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
