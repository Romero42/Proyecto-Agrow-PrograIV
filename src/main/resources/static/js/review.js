/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */
// review.js - Funcionalidad específica para gestión de reseñas

document.addEventListener('DOMContentLoaded', function () {
    // Configuración de SweetAlert para el estilo personalizado de Agrow
    const swalAgrow = Swal.mixin({
        customClass: {
            popup: 'swal2-agrow-popup',
            title: 'swal2-agrow-title',
            confirmButton: 'btn btn-primary swal2-agrow-confirm',
            cancelButton: 'btn btn-danger swal2-agrow-cancel',
            htmlContainer: 'swal2-html-container'
        },
        buttonsStyling: false
    });

    // Inicializar el datepicker para campos de fecha en reseñas
    function initReviewDatepicker() {
        if (document.getElementById('reviewDate')) {
            flatpickr("#reviewDate", {
                dateFormat: "Y-m-d",
                maxDate: "today",
                locale: {
                    firstDayOfWeek: 1
                }
            });
        }
    }
    initReviewDatepicker();

    // Manejo de la selección de estrellas para calificación
    function initRatingStars() {
        const ratingStars = document.getElementById('ratingStars');
        const ratingValue = document.getElementById('ratingValue');

        if (ratingStars && ratingValue) {
            // Inicializar las estrellas según el valor existente
            const initialRating = parseFloat(ratingValue.value) || 0;
            updateStars(initialRating);

            // Agregar eventos a las estrellas
            const stars = ratingStars.querySelectorAll('.star');
            stars.forEach(star => {
                star.addEventListener('click', function () {
                    const value = parseFloat(this.getAttribute('data-value'));
                    ratingValue.value = value;
                    updateStars(value);
                });

                star.addEventListener('mouseover', function () {
                    const value = parseFloat(this.getAttribute('data-value'));
                    highlightStars(value);
                });

                star.addEventListener('mouseout', function () {
                    const selectedValue = parseFloat(ratingValue.value) || 0;
                    updateStars(selectedValue);
                });
            });
        }
    }
    initRatingStars();

    // Función para actualizar la visualización de las estrellas según el valor
    function updateStars(value) {
        const ratingStars = document.getElementById('ratingStars');
        if (!ratingStars)
            return;

        const stars = ratingStars.querySelectorAll('.star');
        stars.forEach(star => {
            const starValue = parseFloat(star.getAttribute('data-value'));
            star.classList.remove('selected');

            if (starValue <= value) {
                star.classList.add('selected');
            }
        });
    }

    // Función para resaltar estrellas en hover
    function highlightStars(value) {
        const ratingStars = document.getElementById('ratingStars');
        if (!ratingStars)
            return;

        const stars = ratingStars.querySelectorAll('.star');
        stars.forEach(star => {
            const starValue = parseFloat(star.getAttribute('data-value'));
            star.classList.remove('selected');

            if (starValue <= value) {
                star.classList.add('selected');
            }
        });
    }

    // Manejo de confirmaciones para eliminación de reseñas
    function attachConfirmHandlers() {
        document.querySelectorAll('.confirm-action').forEach(form => {
            if (form.dataset.confirmAttached)
                return;
            form.dataset.confirmAttached = 'true';

            form.addEventListener('submit', function (e) {
                e.preventDefault();

                const message = this.getAttribute('data-message') || '¿Está seguro de realizar esta acción?';
                const title = this.getAttribute('data-title') || 'Confirmar acción';

                swalAgrow.fire({
                    title: title,
                    text: message,
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Sí, continuar',
                    cancelButtonText: 'Cancelar'
                }).then((result) => {
                    if (result.isConfirmed) {
                        this.submit();
                    }
                });
            });
        });
    }
    attachConfirmHandlers();

    // Petición AJAX genérica para cargar contenido dinámico
    function handleAjaxRequest(url, contentDivId) {
        const contentDiv = document.getElementById(contentDivId);
        if (!contentDiv) {
            console.error(`Contenedor '${contentDivId}' no encontrado.`);
            swalAgrow.fire('Error', `Error interno: Contenedor '${contentDivId}' no encontrado.`, 'error');
            return;
        }

        const loader = document.createElement('div');
        loader.innerHTML = '<div style="text-align:center; padding: 20px;"><span class="material-symbols-outlined" style="font-size: 40px; animation: spin 1.5s linear infinite;">autorenew</span><p>Cargando...</p></div>';
        contentDiv.innerHTML = '';
        contentDiv.appendChild(loader);
        contentDiv.style.opacity = '0.7';

        fetch(url, {headers: {'X-Requested-With': 'XMLHttpRequest'}})
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(`HTTP error ${response.status}: ${text || response.statusText}`);
                        });
                    }
                    return response.text();
                })
                .then(html => {
                    contentDiv.innerHTML = html;
                    contentDiv.style.opacity = '1';
                    attachConfirmHandlers(); // Reaplicar confirmación
                    initReviewDatepicker(); // Reinicializar datepicker
                    initRatingStars(); // Reinicializar estrellas
                })
                .catch(error => {
                    console.error(`Error al cargar contenido para ${contentDivId}:`, error);
                    let errorMsg = 'No se pudo cargar el contenido. Verifique la conexión o inténtelo más tarde.';
                    if (error.message.includes('HTTP error')) {
                        errorMsg = `Error al cargar: ${error.message}. Por favor, intente de nuevo.`;
                    }
                    swalAgrow.fire('Error de Carga', errorMsg, 'error');
                    contentDiv.innerHTML = `<div class="info-card"><span class="material-symbols-outlined icon">error</span><h2>Error de Carga</h2><p>No se pudieron cargar los datos (${error.message}). Por favor, actualice o intente de nuevo.</p></div>`;
                    contentDiv.style.opacity = '1';
                });
    }

    // Paginación para reseñas
    window.pageReviews = function (link) {
        if (!link)
            return;
        const page = link.getAttribute('data-page');
        const formData = new FormData(document.getElementById('filter-form-review') || new FormData());
        formData.set('page', page);
        handleAjaxRequest(`/reviews/table?${new URLSearchParams(formData).toString()}`, 'review-list-content');
    };

    // Eventos dinámicos para contenido AJAX de reseñas
    function attachDynamicEventListeners() {
        // Manejo de formularios de filtro
        document.body.addEventListener('submit', function (event) {
            const form = event.target.closest('form.filter-section');
            if (!form || form.id !== 'filter-form-review')
                return;

            // Prevenir el envío normal del formulario
            event.preventDefault();

            const formData = new FormData(form);
            formData.set('page', '0'); // Al filtrar, siempre ir a la página 0

            // Ejecutar la petición AJAX
            handleAjaxRequest(`/reviews/table?${new URLSearchParams(formData).toString()}`, 'review-list-content');
        });

        // Manejo de paginación
        document.body.addEventListener('click', function (event) {
            const pgLink = event.target.closest('.pagination a[data-page]');
            if (pgLink) {
                event.preventDefault();
                const container = pgLink.closest('[id="review-list-content"]');
                if (container) {
                    window.pageReviews(pgLink);
                }
            }
        });
    }
    attachDynamicEventListeners();

    // Mostrar mensajes de alerta
    const swalMessage = document.getElementById('swal-message');
    if (swalMessage) {
        const mensaje = swalMessage.getAttribute('data-mensaje');
        const error = swalMessage.getAttribute('data-error');

        if (mensaje && mensaje !== '') {
            swalAgrow.fire({
                title: '¡Operación exitosa!',
                text: mensaje,
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        }

        if (error && error !== '') {
            swalAgrow.fire({
                title: '¡Error!',
                text: error,
                icon: 'error',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        }
    }
});

document.querySelector('form').addEventListener('submit', function (e) {
    const dateValue = document.getElementById('reviewDate').value.trim();
    if (!dateValue) {
        e.preventDefault();
        Swal.fire('Error', 'Debe seleccionar una fecha de reseña.', 'error');
    }
});

const ratingValue = document.getElementById('ratingValue');
if (!ratingValue.value) {
    isValid = false;
    errorMessage += '- Debe seleccionar una calificación (estrellas).<br>';
    document.getElementById('ratingStars').classList.add('error-input');
} else {
    document.getElementById('ratingStars').classList.remove('error-input');
}

