package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
