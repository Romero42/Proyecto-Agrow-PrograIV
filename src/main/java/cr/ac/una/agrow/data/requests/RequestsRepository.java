package cr.ac.una.agrow.data.requests;

import cr.ac.una.agrow.domain.requests.Requests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestsRepository extends JpaRepository<Requests, Integer> {
    // Find requests by name or type (for search functionality)
    @Query("SELECT r FROM Requests r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.type) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Requests> findByNameOrTypeContainingIgnoreCase(@Param("searchTerm") String searchTerm);
}
