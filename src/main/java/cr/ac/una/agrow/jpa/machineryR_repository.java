package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.machineryRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface machineryR_repository extends JpaRepository<machineryRental, Integer> {

    List<machineryRental> findByRentStartDayBetween(LocalDate start, LocalDate end);
    List<machineryRental> findByRentStartDayGreaterThanEqual(LocalDate start);
    List<machineryRental> findByRentFinalDayLessThanEqual(LocalDate end);

    @Query(
            value       = "SELECT * FROM machinaryrental WHERE id_maquina = :idMaquina",
            nativeQuery = true
    )
    Optional<machineryRental> findFirstByIdMaquina(@Param("idMaquina") int idMaquina);

}
