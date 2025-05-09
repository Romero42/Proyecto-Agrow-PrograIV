
package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Supply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Integer> {

    @Query("SELECT s FROM Supply s LEFT JOIN FETCH s.supplier sup LEFT JOIN FETCH s.createdByUser cbUser " +
            "WHERE (:searchTerm IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:category IS NULL OR :category = '' OR s.category = :category) AND " +
            "(:supplierId IS NULL OR sup.supplierIdentification = :supplierId) " +
            "ORDER BY (CASE WHEN cbUser.id_User = :currentUserId THEN 0 ELSE 1 END), s.name ASC")
    Page<Supply> findFilteredSuppliesWithOwnerPriority(@Param("searchTerm") String searchTerm,
                                                       @Param("category") String category,
                                                       @Param("supplierId") Integer supplierId,
                                                       @Param("currentUserId") Integer currentUserId,
                                                       Pageable pageable);

    @Override
    @Query("SELECT s FROM Supply s LEFT JOIN FETCH s.supplier sup LEFT JOIN FETCH s.createdByUser cbUser")
    Page<Supply> findAll(Pageable pageable);
}
