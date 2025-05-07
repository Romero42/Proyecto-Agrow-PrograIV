package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.machineryRental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface machineryR_repository extends JpaRepository<machineryRental, Integer> {

    Page<machineryRental> findByRentStartDayBetween(LocalDate start, LocalDate end, Pageable pageable);
    Page<machineryRental> findByRentStartDayGreaterThanEqual(LocalDate start, Pageable pageable);
    Page<machineryRental> findByRentFinalDayLessThanEqual(LocalDate end, Pageable pageable);
    Page<machineryRental> findAll(Pageable pageable);

    @Query(
            value       = "SELECT * FROM machinaryrental WHERE id_maquina = :idMaquina",
            nativeQuery = true
    )
    Optional<machineryRental> findFirstByIdMaquina(@Param("idMaquina") int idMaquina);

}
