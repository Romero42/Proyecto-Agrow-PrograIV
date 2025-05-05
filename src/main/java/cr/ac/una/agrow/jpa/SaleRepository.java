package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {

    // Método para encontrar ventas por ID de cosecha
    List<Sale> findByHarvestIdHarvest(int harvestId);

    // Método para encontrar ventas paginadas
    Page<Sale> findAll(Pageable pageable);

}