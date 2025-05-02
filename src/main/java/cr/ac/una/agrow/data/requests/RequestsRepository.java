package cr.ac.una.agrow.data.requests;

import cr.ac.una.agrow.domain.requests.Requests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestsRepository extends JpaRepository<Requests, Integer> {
}
