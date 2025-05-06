package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.requests.Requests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestsRepository extends JpaRepository<Requests, Integer> {
    // Búsqueda por nombre o tipo con paginación
    @Query("SELECT r FROM Requests r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.status) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Requests> findByNameOrTypeContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    //para asegurar que usa paginación
    @Override
    Page<Requests> findAll(Pageable pageable);
}
