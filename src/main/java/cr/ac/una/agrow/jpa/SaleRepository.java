// src/main/java/cr/ac/una/agrow/jpa/SaleRepository.java
package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {

    List<Sale> findByHarvestIdHarvest(int harvestId);


    @Query("SELECT s FROM Sale s LEFT JOIN FETCH s.harvest " +
            "WHERE (:saleDate IS NULL OR s.saleDate = :saleDate) " +
            "AND (:buyerName IS NULL OR :buyerName = '' OR LOWER(s.buyerName) LIKE LOWER(CONCAT('%', :buyerName, '%')))")
    Page<Sale> findFilteredSales(@Param("saleDate") LocalDate saleDate,
                                 @Param("buyerName") String buyerName,
                                 Pageable pageable);
}