package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.harvest.Harvest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// Procedure import might not be needed if using @Query for filtering
// import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface HarvestRepository extends JpaRepository<Harvest, Integer> {

    @Query("SELECT h FROM Harvest h WHERE " +
            "(:quality IS NULL OR :quality = '' OR h.quality = :quality) AND " +
            "(:destiny IS NULL OR :destiny = '' OR LOWER(h.destiny) LIKE LOWER(CONCAT('%', :destiny, '%')))")
    Page<Harvest> findFilteredHarvests(@Param("quality") String quality,
                                       @Param("destiny") String destiny,
                                       Pageable pageable);

    // Keep other existing queries
    @Query("SELECT h FROM Harvest h WHERE h.id_producer = :producerId AND h.availableQuantity > 0")
    List<Harvest> findAvailableByProducerId(@Param("producerId") int producerId);

    @Query("SELECT h FROM Harvest h WHERE h.id_producer = :producerId")
    List<Harvest> findAllByProducerId(@Param("producerId") int producerId);

}