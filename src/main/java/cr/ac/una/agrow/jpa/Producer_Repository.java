package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.producer.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Producer_Repository extends JpaRepository<Producer, Integer> {

    Page<Producer> findAll(Pageable pageable);
    Page<Producer> findByCity(String city, Pageable pageable);
}
