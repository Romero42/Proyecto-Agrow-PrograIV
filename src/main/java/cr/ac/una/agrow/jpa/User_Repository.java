package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface User_Repository extends JpaRepository<User, Integer> {

    Page<User> findAll(Pageable pageable);
    Page<User> findByType(String type, Pageable pageable);
    Optional<User> findByUser(String user);
}
