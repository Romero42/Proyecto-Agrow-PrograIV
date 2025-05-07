package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.machinery.Machinery;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineryRepository extends JpaRepository<Machinery, Integer> {
    List<Machinery> findByDisponibilidad(boolean disponibilidad);
    List<Machinery> findByNombreContainingIgnoreCase(String nombre);
    Optional<Machinery> findById(Integer id);
}
