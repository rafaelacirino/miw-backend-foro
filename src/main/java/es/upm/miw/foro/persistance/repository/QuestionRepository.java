package es.upm.miw.foro.persistance.repository;

import es.upm.miw.foro.persistance.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Question> findByTitleContainingIgnoreCase(String title);

    Optional<Question> findByTitle(String title);
}
