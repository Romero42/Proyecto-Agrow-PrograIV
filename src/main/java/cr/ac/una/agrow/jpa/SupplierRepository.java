
package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.supplier.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findAllByOrderByCompanyNameAsc();
    // Metodo para buscar proveedores por nombre de contacto o empresa
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.supplierName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Supplier> findBySupplierNameOrCompanyNameContainingIgnoreCase(@Param("search") String search);

    // Búsqueda por nombre o tipo con paginación
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.supplierName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Supplier> findByNameOrTypeContainingIgnoreCase(@Param("search") String search, Pageable pageable);

}