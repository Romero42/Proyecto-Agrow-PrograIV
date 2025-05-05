package com.agrow.repository;

import cr.ac.una.agrow.domain.Review;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



/**
 * Repositorio para la entidad Review
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Busca reseñas por tipo
     * @param reviewType El tipo de reseña
     * @return Lista de reseñas del tipo especificado
     */
    List<Review> findByReviewType(String reviewType);
    
    /**
     * Busca reseñas por calificación mínima
     * @param rating Calificación mínima
     * @return Lista de reseñas con calificación mayor o igual a la especificada
     */
    List<Review> findByRatingGreaterThanEqual(Double rating);
    
    /**
     * Busca reseñas por tipo y calificación mínima
     * @param reviewType El tipo de reseña
     * @param rating Calificación mínima
     * @return Lista de reseñas que coinciden con ambos criterios
     */
    List<Review> findByReviewTypeAndRatingGreaterThanEqual(String reviewType, Double rating);
    
    /**
     * Obtiene las reseñas verificadas
     * @return Lista de reseñas verificadas
     */
    List<Review> findByVerifiedTrue();
    
    /**
     * Obtiene las reseñas no verificadas
     * @return Lista de reseñas no verificadas
     */
    List<Review> findByVerifiedFalse();
    
    /**
     * Obtiene las reseñas con recomendación positiva
     * @return Lista de reseñas recomendadas
     */
    List<Review> findByRecommendationStatusTrue();
    
    /**
     * Busca reseñas por nombre del cliente
     * @param name Parte del nombre a buscar
     * @return Lista de reseñas que coinciden con el nombre parcial
     */
    List<Review> findByReviewerNameContainingIgnoreCase(String name);
    
    /**
     * Filtra reseñas por tipo y estado de verificación
     * @param reviewType El tipo de reseña
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas
     */
    List<Review> findByReviewTypeAndVerified(String reviewType, Boolean verified);
    
    /**
     * Filtra reseñas por calificación mínima y estado de verificación
     * @param rating Calificación mínima
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas
     */
    List<Review> findByRatingGreaterThanEqualAndVerified(Double rating, Boolean verified);
    
    /**
     * Filtra reseñas por tipo, calificación mínima y estado de verificación
     * @param reviewType El tipo de reseña
     * @param rating Calificación mínima
     * @param verified Estado de verificación
     * @return Lista de reseñas filtradas por todos los criterios
     */
    List<Review> findByReviewTypeAndRatingGreaterThanEqualAndVerified(String reviewType, Double rating, Boolean verified);
    
    /**
     * Busca reseñas asociadas a una venta específica
     * @param sale La venta relacionada
     * @return Lista de reseñas para la venta especificada
     */
    List<Review> findBySale(Sale sale);
    
    /**
     * Busca reseñas por ID de venta
     * @param saleId ID de la venta
     * @return Lista de reseñas para la venta especificada
     */
    List<Review> findBySaleId(Integer saleId);
    
    /**
     * Obtiene las reseñas ordenadas por fecha (más recientes primero)
     * @return Lista de reseñas ordenadas por fecha
     */
    List<Review> findAllByOrderByReviewDateDesc();
    
    /**
     * Obtiene las reseñas ordenadas por calificación (mejores primero)
     * @return Lista de reseñas ordenadas por calificación
     */
    List<Review> findAllByOrderByRatingDesc();
}
