package cr.ac.una.agrow.service.review;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.una.agrow.domain.Review;
import cr.ac.una.agrow.domain.Sale;
import cr.ac.una.agrow.jpa.ReviewRepository;
import cr.ac.una.agrow.service.HarvestService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para gestionar operaciones relacionadas con las reseñas de ventas
 */
@Service
public class ReviewService {

    private static final Logger LOG = Logger.getLogger(ReviewService.class.getName());

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Obtiene todas las reseñas
     *
     * @return Lista de todas las reseñas
     */
    @Transactional
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    /**
     * Busca una reseña por su ID
     *
     * @param id ID de la reseña
     * @return Optional con la reseña encontrada o vacío si no existe
     */
    @Transactional
    public Optional<Review> findById(Integer id) {
        return reviewRepository.findById(id);
    }

    /**
     * Guarda o actualiza una reseña
     *
     * @param review La reseña a guardar
     * @return La reseña guardada con su ID asignado
     */
    @Transactional
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    /**
     * Elimina una reseña por su ID
     *
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

    /**
     * Obtiene reseñas filtradas por calificación mínima de forma paginada
     *
     * @param minRating Calificación mínima (opcional)
     * @param pageable Configuración de paginación
     * @return Página de reseñas filtradas
     */
    @Transactional(readOnly = true)
    public Page<Review> getFilteredReviewsPaged(Integer minRating, Pageable pageable) {
        try {
            return reviewRepository.findByRatingGreaterThanEqual(minRating != null ? minRating : 0, pageable);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error al obtener reseñas filtradas", ex);
            return Page.empty(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<Review> getAllPaged(Pageable pageable) {
        return getFilteredReviewsPaged(null, pageable);
    }
}
