package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Question;
import es.upm.miw.foro.persistence.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t JOIN t.questions q WHERE q.id = :questionId ORDER BY t.name ASC")
    List<Tag> findByQuestionId(@Param("questionId") Long questionId);


    List<Tag> findByQuestionsContaining(Question question);

    List<Tag> findByNameContainingIgnoreCase(String query);
}
