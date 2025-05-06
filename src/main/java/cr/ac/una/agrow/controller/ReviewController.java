package cr.ac.una.agrow.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cr.ac.una.agrow.domain.Review;
import cr.ac.una.agrow.service.review.ReviewService;

/**
 * Controlador para gestionar las reseñas en el sistema Agrow
 */
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Muestra la lista de todas las reseñas
     *
     * @param model Modelo para la vista
     * @return Vista de listado de reseñas
     */
    
    @GetMapping("/list")
    public String listReviews(Model model,
            @RequestParam(value = "minRating", required = false) String minRating) {
        
        List<Review> reviewList = reviewService.findAll();
        if (minRating != null) {
            reviewList = reviewService.getMinRatingReviews(Integer.parseInt(minRating));
        } else {
            reviewList = reviewService.findAll();
        }
        model.addAttribute("reviewList", reviewList);

        return "list_review";
    }

    /**
     * Muestra el formulario para crear una nueva reseña
     *
     * @param model Modelo para la vista
     * @return Vista del formulario de reseña
     */
    @GetMapping("/form")
    public String showForm(Model model) {
        // Crear una nueva instancia de reseña con valores predeterminados
        Review review = new Review();
        review.setReviewDate(LocalDate.now());
        review.setVerified(false);
        review.setRecommendationStatus(false);

        model.addAttribute("review", review);
        return "form_review";
    }

    /**
     * Muestra el formulario para editar una reseña existente
     *
     * @param reviewId ID de la reseña a editar
     * @param model Modelo para la vista
     * @param redirectAttributes Atributos para mensajes de redirección
     * @return Vista del formulario de reseña o redirección a la lista
     */
    @GetMapping("/edit")
    public String editReview(
            @RequestParam Integer reviewId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Review> optionalReview = reviewService.findById(reviewId);

        if (optionalReview.isPresent()) {
            model.addAttribute("review", optionalReview.get());
            return "reviews/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "La reseña no fue encontrada");
            return "redirect:/list_review";
        }
    }

    /**
     * Muestra los detalles de una reseña específica
     *
     * @param reviewId ID de la reseña a mostrar
     * @param model Modelo para la vista
     * @param redirectAttributes Atributos para mensajes de redirección
     * @return Vista de detalles de reseña o redirección a la lista
     */
    @GetMapping("/view")
    public String viewReview(
            @RequestParam Integer reviewId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Review> optionalReview = reviewService.findById(reviewId);

        if (optionalReview.isPresent()) {
            model.addAttribute("review", optionalReview.get());
            return "reviews/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "La reseña no fue encontrada");
            return "redirect:/view";
        }
    }

    /**
     * Guarda o actualiza una reseña
     *
     * @param review Reseña a guardar o actualizar
     * @param result Resultados de la validación del formulario
     * @param model Modelo para la vista
     * @param redirectAttributes Atributos para mensajes de redirección
     * @return Redirección a la lista de reseñas o volver al formulario si hay
     * errores
     */
    @PostMapping("/save")
    public String saveReview(
            @ModelAttribute Review review,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validar campos requeridos
        if (review.getReviewerName() == null || review.getReviewerName().trim().isEmpty()) {
            result.rejectValue("reviewerName", "error.reviewerName", "El nombre del revisor es obligatorio");
        }

        if (review.getReviewType() == null || review.getReviewType().trim().isEmpty()) {
            result.rejectValue("reviewType", "error.reviewType", "El tipo de reseña es obligatorio");
        }

        if (review.getRating() == null) {
            result.rejectValue("rating", "error.rating", "La calificación es obligatoria");
        }

        if (review.getReviewDate() == null) {
            result.rejectValue("reviewDate", "error.reviewDate", "La fecha de reseña es obligatoria");
        }

        // Si hay errores, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("review", review);
            return "reviews/form";
        }

        // Asegurar valores por defecto para campos booleanos si son null
        if (review.getVerified() == null) {
            review.setVerified(false);
        }

        if (review.getRecommendationStatus() == null) {
            review.setRecommendationStatus(false);
        }

        // Guardar la reseña
        try {
            reviewService.save(review);
            redirectAttributes.addFlashAttribute("mensaje", "Reseña guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la reseña: " + e.getMessage());
        }

        return "list_review";
    }

    /**
     * Elimina una reseña
     *
     * @param reviewId ID de la reseña a eliminar
     * @param redirectAttributes Atributos para mensajes de redirección
     * @return Redirección a la lista de reseñas
     */
    @PostMapping("/delete")
    public String deleteReview(
            @RequestParam Integer reviewId,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Review> optionalReview = reviewService.findById(reviewId);

            if (optionalReview.isPresent()) {
                reviewService.deleteById(reviewId);
                redirectAttributes.addFlashAttribute("mensaje", "Reseña eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "La reseña no fue encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la reseña: " + e.getMessage());
        }
        return "list_review";
    }
}
