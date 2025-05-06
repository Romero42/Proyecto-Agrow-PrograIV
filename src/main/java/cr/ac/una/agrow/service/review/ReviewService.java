package cr.ac.una.agrow.service.review;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.una.agrow.domain.Review;
import cr.ac.una.agrow.domain.Sale;
import cr.ac.una.agrow.jpa.ReviewRepository;

/**
 * Servicio para gestionar operaciones relacionadas con las reseñas de ventas
 */
@Service
public class ReviewService {
   
    @Autowired
    private ReviewRepository reviewRepository;
    
    /**
     * Obtiene todas las reseñas
     * @return Lista de todas las reseñas
     */
    @Transactional
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    /**
     * Busca una reseña por su ID
     * @param id ID de la reseña
     * @return Optional con la reseña encontrada o vacío si no existe
     */
    @Transactional
    public Optional<Review> findById(Integer id) {
        return reviewRepository.findById(id);
    }
    
    /**
     * Guarda o actualiza una reseña
     * @param review La reseña a guardar
     * @return La reseña guardada con su ID asignado
     */
    @Transactional
    public Review save(Review review) {
        return reviewRepository.save(review);
    }
    
    /**
     * Elimina una reseña por su ID
     * @param id ID de la reseña a eliminar
     */
    @Transactional
    public void deleteById(Integer id) {
        reviewRepository.deleteById(id);
    }
    
    @Transactional
    public List<Review> getMinRatingReviews(int rating) {
        return reviewRepository.sp_getMinRating(rating);
    }
}