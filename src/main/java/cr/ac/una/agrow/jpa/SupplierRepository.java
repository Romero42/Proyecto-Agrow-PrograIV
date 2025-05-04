
package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.supplier.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findAllByOrderByCompanyNameAsc();

    List<Supplier> findByCompanyNameContainingIgnoreCaseOrSupplierNameContainingIgnoreCaseOrSupplierIdentification(String company, String name, int id);

}