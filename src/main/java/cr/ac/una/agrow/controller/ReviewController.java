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
import cr.ac.una.agrow.domain.Sale;
import cr.ac.una.agrow.service.SaleService;
import cr.ac.una.agrow.service.review.ReviewService;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador para gestionar las reseñas en el sistema Agrow
 */
@Controller
@RequestMapping("/reviews")
public class ReviewController {
    
    private static final Logger LOG = Logger.getLogger(ReviewController.class.getName());

    private static final int PAGE_SIZE = 5;
    private static final int MAX_VISIBLE_PAGINATION_LINKS = 5;
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SaleService saleService;

    /**
     * Muestra la lista de todas las reseñas
     *
     * @param model Modelo para la vista
     * @return Vista de listado de reseñas
     *//*
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
    } */
    
    @GetMapping("/list")
    public String listReviews(Model model,
            @RequestParam(value = "minRating", required = false) Integer minRating,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Review> reviewsPage = reviewService.getFilteredReviewsPaged(minRating, pageable);
        prepareModelForView(model, reviewsPage, minRating, page);
        model.addAttribute("activeModule", "reviews");
        model.addAttribute("activePage", "list");
        return "list_review";
    }
    @GetMapping("/table")
    public String getReviewTable(Model model,
            @RequestParam(value = "minRating", required = false) Integer minRating,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Review> reviewsPage = reviewService.getFilteredReviewsPaged(minRating, pageable);
        prepareModelForView(model, reviewsPage, minRating, page);
        return "review/table_review :: reviewListContent";
    }

    private void prepareModelForView(Model model, Page<Review> pageData,
            Integer minRating, int page) {
        int totalPages = pageData.getTotalPages();
        int start = Math.max(0, page - MAX_VISIBLE_PAGINATION_LINKS / 2);
        int end = Math.min(totalPages - 1, start + MAX_VISIBLE_PAGINATION_LINKS - 1);

        if (totalPages > MAX_VISIBLE_PAGINATION_LINKS && (end - start + 1) < MAX_VISIBLE_PAGINATION_LINKS) {
            if (start == 0) {
                end = MAX_VISIBLE_PAGINATION_LINKS - 1;
            } else if (end == totalPages - 1) {
                start = totalPages - MAX_VISIBLE_PAGINATION_LINKS;
            }
        }
        if (totalPages == 0) {
            start = 0;
            end = -1;
        }

        model.addAttribute("reviewList", pageData.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", pageData.getTotalElements());
        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);
        model.addAttribute("minRating", minRating);
        if (pageData.isEmpty()) {
            model.addAttribute("validate", (minRating != null)
                    ? "No se encontraron reseñas con esos filtros."
                    : "No hay reseñas registradas.");
        }
    }
    
    @GetMapping("/form/sale/{saleId}")
    public String showFormWithSale(@PathVariable Integer saleId, Model model) {
        // Crear una nueva instancia de reseña con valores predeterminados
        Review review = new Review();
        review.setReviewDate(LocalDate.now());
        review.setVerified(false);
        review.setRecommendationStatus(false);
        review.setIdSale(saleId); // Asociar el ID de venta a la reseña

        
        Optional<Sale> optionalSale = saleService.getSaleById(saleId);
        if (optionalSale.isPresent()) {
            Sale sale = optionalSale.get();
            model.addAttribute("sale", sale);

            review.setReviewerName(sale.getBuyerName());
        }

        model.addAttribute("review", review);
        return "form_review";
    }

    @GetMapping("/edit")
    public String editReview(
            @RequestParam Integer reviewId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Review> optionalReview = reviewService.findById(reviewId);
        if (optionalReview.isPresent()) {
            model.addAttribute("review", optionalReview.get());
            return "edit_review"; // Cambia a tu nueva plantilla
        } else {
            redirectAttributes.addFlashAttribute("error", "La reseña no fue encontrada");
            return "redirect:/reviews/list";
        }
    }

    @GetMapping("/view")
    public String viewReview(
            @RequestParam Integer reviewId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Review> optionalReview = reviewService.findById(reviewId);

        if (optionalReview.isPresent()) {
            model.addAttribute("review", optionalReview.get());
            return "view";
        } else {
            redirectAttributes.addFlashAttribute("error", "La reseña no fue encontrada");
            return "view";
        }
    }
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
            return "form_review";
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

        return "redirect:/reviews/list";
    }

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
        return "redirect:/reviews/list";
    }
}
