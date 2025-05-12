package es.upm.miw.foro.persistence.repository;

import es.upm.miw.foro.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Page<User> findByFirstName(String name, Pageable pageable);

    Page<User> findByFirstNameAndLastNameAndEmail(String firstName, String lastName, String email, Pageable pageable);

    Page<User> findByLastName(String lastName, Pageable pageable);

    Page<User> findByEmail(String email, Pageable pageable);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);
}
