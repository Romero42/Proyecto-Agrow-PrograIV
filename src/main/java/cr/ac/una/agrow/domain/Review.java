/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Modelo de entidad para las reseñas de ventas.
 */
@Entity
@Table(name = "sale_reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer reviewId;

    private String reviewerName;
    

    private String reviewType; // Tipos: Venta Directa, Distribución, Exportación, etc.
    private Double rating;
    private String comment;
    private LocalDate reviewDate;
    private Boolean verified; // Indica si la reseña ha sido verificada
    private Boolean recommendationStatus; // Indica si el cliente recomendaría nuestros productos/servicios
    
    
    // Constructor vacío
    public Review() {
        this.reviewDate = LocalDate.now();
        this.verified = false;
        this.recommendationStatus = false;
    }
    
    // Constructor con parámetros
    public Review(String reviewerName, String reviewType, Double rating, String comment, 
                 LocalDate reviewDate, Boolean verified, Boolean recommendationStatus) {
        this.reviewerName = reviewerName;
        this.reviewType = reviewType;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.verified = verified;
        this.recommendationStatus = recommendationStatus;
    }
    /*
    // Constructor con parámetros incluyendo sale
    public Review(String reviewerName, String reviewType, Double rating, String comment, 
                 LocalDate reviewDate, Boolean verified, Boolean recommendationStatus, Sale sale) {
        this.reviewerName = reviewerName;
        this.reviewType = reviewType;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.verified = verified;
        this.recommendationStatus = recommendationStatus;
        this.sale = sale;
    }*/

    // Getters y Setters
    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getRecommendationStatus() {
        return recommendationStatus;
    }

    public void setRecommendationStatus(Boolean recommendationStatus) {
        this.recommendationStatus = recommendationStatus;
    }
    /*
    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }*/
}