package cr.ac.una.agrow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrow.repository.ReviewRepository;
import cr.ac.una.agrow.domain.Review;

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
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
    
    /**
     * Busca una reseña por su ID
     * @param id ID de la reseña
     * @return Optional con la reseña encontrada o vacío si no existe
     */
    @Transactional(readOnly = true)
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
    
    /**
     * Filtra reseñas por tipo de venta
     * @param reviewType El tipo de venta
     * @return Lista de reseñas del tipo especificado
     */
    @Transactional(readOnly = true)
    public List<Review> findByReviewType(String reviewType) {
        return reviewRepository.findByReviewType(reviewType);
    }
    
    /**
     * Filtra reseñas por calificación mínima
     * @param minRating Calificación mínima
     * @return Lista de reseñas con calificación mayor o igual a la especificada
     */
    @Transactional(readOnly = true)
    public List<Review> findByMinimumRating(Double minRating) {
        return reviewRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    /**
     * Filtra reseñas por tipo y calificación mínima
     * @param reviewType El tipo de venta
     * @param minRating Calificación mínima
     * @return Lista de reseñas que coinciden con ambos criterios
     */
    @Transactional(readOnly = true)
    public List<Review> findByTypeAndMinimumRating(String reviewType, Double minRating) {
        return reviewRepository.findByReviewTypeAndRatingGreaterThanEqual(reviewType, minRating);
    }
    
    /**
     * Obtiene las reseñas verificadas
     * @return Lista de reseñas verificadas
     */
    @Transactional(readOnly = true)
    public List<Review> findVerifiedReviews() {
        return reviewRepository.findByVerifiedTrue();
    }
    
    /**
     * Obtiene las reseñas no verificadas
     * @return Lista de reseñas no verificadas
     */
    @Transactional(readOnly = true)
    public List<Review> findUnverifiedReviews() {
        return reviewRepository.findByVerifiedFalse();
    }
    
    /**
     * Obtiene las reseñas con recomendación positiva
     * @return Lista de reseñas recomendadas
     */
    @Transactional(readOnly = true)
    public List<Review> findRecommendedReviews() {
        return reviewRepository.findByRecommendationStatusTrue();
    }
    
    /**
     * Busca reseñas por nombre del cliente
     * @param name Parte del nombre a buscar
     * @return Lista de reseñas que coinciden con el nombre parcial
     */
    @Transactional(readOnly = true)
    public List<Review> findByReviewerNameContaining(String name) {
        return reviewRepository.findByReviewerNameContainingIgnoreCase(name);
    }
    
    /**
     * Filtra reseñas por tipo y estado de verificación
     * @param reviewType El tipo de venta
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas
     */
    @Transactional(readOnly = true)
    public List<Review> findByTypeAndVerified(String reviewType, Boolean verified) {
        return reviewRepository.findByReviewTypeAndVerified(reviewType, verified);
    }
    
    /**
     * Filtra reseñas por calificación mínima y estado de verificación
     * @param minRating Calificación mínima
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas
     */
    @Transactional(readOnly = true)
    public List<Review> findByRatingAndVerified(Double minRating, Boolean verified) {
        return reviewRepository.findByRatingGreaterThanEqualAndVerified(minRating, verified);
    }
    
    /**
     * Filtra reseñas por tipo, calificación mínima y estado de verificación
     * @param reviewType El tipo de venta
     * @param minRating Calificación mínima
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas por todos los criterios
     */
    @Transactional(readOnly = true)
    public List<Review> findByTypeRatingAndVerified(String reviewType, Double minRating, Boolean verified) {
        return reviewRepository.findByReviewTypeAndRatingGreaterThanEqualAndVerified(reviewType, minRating, verified);
    }
    
    /**
     * Busca reseñas asociadas a una venta específica
     * @param sale La venta relacionada
     * @return Lista de reseñas para la venta especificada
     */
    @Transactional(readOnly = true)
    public List<Review> findBySale(Sale sale) {
        return reviewRepository.findBySale(sale);
    }
    
    /**
     * Busca reseñas por ID de venta
     * @param saleId ID de la venta
     * @return Lista de reseñas para la venta especificada
     */
    @Transactional(readOnly = true)
    public List<Review> findBySaleId(Integer saleId) {
        return reviewRepository.findBySaleId(saleId);
    }
    
    /**
     * Obtiene las reseñas ordenadas por fecha (más recientes primero)
     * @return Lista de reseñas ordenadas por fecha
     */
    @Transactional(readOnly = true)
    public List<Review> findAllOrderByDateDesc() {
        return reviewRepository.findAllByOrderByReviewDateDesc();
    }
    
    /**
     * Obtiene las reseñas ordenadas por calificación (mejores primero)
     * @return Lista de reseñas ordenadas por calificación
     */
    @Transactional(readOnly = true)
    public List<Review> findAllOrderByRatingDesc() {
        return reviewRepository.findAllByOrderByRatingDesc();
    }
}
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
    
    /**
     * Filtra reseñas por tipo de venta
     * @param reviewType El tipo de venta
     * @return Lista de reseñas del tipo especificado
     */
    @Transactional(readOnly = true)
    public List<Review> findByReviewType(String reviewType) {
        return reviewRepository.findByReviewType(reviewType);
    }
    
    /**
     * Filtra reseñas por calificación mínima
     * @param minRating Calificación mínima
     * @return Lista de reseñas con calificación mayor o igual a la especificada
     */
    @Transactional(readOnly = true)
    public List<Review> findByMinimumRating(Double minRating) {
        return reviewRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    /**
     * Filtra reseñas por tipo y calificación mínima
     * @param reviewType El tipo de venta
     * @param minRating Calificación mínima
     * @return Lista de reseñas que coinciden con ambos criterios
     */
    @Transactional(readOnly = true)
    public List<Review> findByTypeAndMinimumRating(String reviewType, Double minRating) {
        return reviewRepository.findByReviewTypeAndRatingGreaterThanEqual(reviewType, minRating);
    }
    
    /**
     * Obtiene las reseñas verificadas
     * @return Lista de reseñas verificadas
     */
    @Transactional(readOnly = true)
    public List<Review> findVerifiedReviews() {
        return reviewRepository.findByVerifiedTrue();
    }
    
    /**
     * Obtiene las reseñas no verificadas
     * @return Lista de reseñas no verificadas
     */
    @Transactional(readOnly = true)
    public List<Review> findUnverifiedReviews() {
        return reviewRepository.findByVerifiedFalse();
    }
    
    /**
     * Obtiene las reseñas con recomendación positiva
     * @return Lista de reseñas recomendadas
     */
    @Transactional(readOnly = true)
    public List<Review> findRecommendedReviews() {
        return reviewRepository.findByRecommendationStatusTrue();
    }
    
    /**
     * Busca reseñas por nombre del cliente
     * @param name Parte del nombre a buscar
     * @return Lista de reseñas que coinciden con el nombre parcial
     */
    @Transactional(readOnly = true)
    public List<Review> findByReviewerNameContaining(String name) {
        return reviewRepository.findByReviewerNameContainingIgnoreCase(name);
    }
    
    /**
     * Filtra reseñas por tipo y estado de verificación
     * @param reviewType El tipo de venta
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas
     */
    @Transactional(readOnly = true)
    public List<Review> findByTypeAndVerified(String reviewType, Boolean verified) {
        return reviewRepository.findByReviewTypeAndVerified(reviewType, verified);
    }
    
    /**
     * Filtra reseñas por calificación mínima y estado de verificación
     * @param minRating Calificación mínima
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas
     */
    @Transactional(readOnly = true)
    public List<Review> findByRatingAndVerified(Double minRating, Boolean verified) {
        return reviewRepository.findByRatingGreaterThanEqualAndVerified(minRating, verified);
    }
    
    /**
     * Filtra reseñas por tipo, calificación mínima y estado de verificación
     * @param reviewType El tipo de venta
     * @param minRating Calificación mínima
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas por todos los criterios
     */
    @Transactional(readOnly = true)
    public List<Review> findByTypeRatingAndVerified(String reviewType, Double minRating, Boolean verified) {
        return reviewRepository.findByReviewTypeAndRatingGreaterThanEqualAndVerified(reviewType, minRating, verified);
    }
}