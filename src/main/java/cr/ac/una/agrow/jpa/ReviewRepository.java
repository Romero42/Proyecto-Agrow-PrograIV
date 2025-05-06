package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Review;
import cr.ac.una.agrow.domain.Sale;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



/**
 * Repositorio para la entidad Review
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Procedure(name = "Review.sp_getMinRating")
    public List<Review> sp_getMinRating(int rating);
    
    @Query("SELECT r FROM Review r WHERE " +
           "(:minRating IS NULL OR :minRating = '' OR r.rating >= CAST(:minRating AS int))")
    Page<Review> findFilteredReviews(@Param("minRating") String minRating, Pageable pageable);
}
