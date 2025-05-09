/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

// harvest.js - Funcionalidad específica para gestión de cosechas

document.addEventListener('DOMContentLoaded', () => {
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

    // Inicialización de Flatpickr para selectores de fecha
    function initFlatpickr() {
        if (!window.flatpickr) {
            console.warn("La librería Flatpickr no está cargada.");
            return;
        }
        // Selector de fecha para cosechas (pasado hasta hoy)
        flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
    }
    initFlatpickr();

    // Formato de moneda (CRC) para mostrar precios en colones
    const formatColones = val => {
        const num = parseFloat(val);
        if (isNaN(num)) return '---';
        return new Intl.NumberFormat('es-CR', {
            style: 'currency',
            currency: 'CRC',
            minimumFractionDigits: 2
        }).format(num);
    };

    // Aplicar formato de precios a elementos con clase price-display
    function formatPrices() {
        document.querySelectorAll('.price-display').forEach(el => {
            const value = el.dataset.value || el.textContent;
            el.textContent = formatColones(value);
        });
    }
    formatPrices();

    // Manejo de diálogos de confirmación para formularios y acciones
    function attachConfirmHandlers() {
        document.querySelectorAll('.confirm-action').forEach(el => {
            if (el.dataset.confirmAttached) return;
            el.dataset.confirmAttached = 'true';

            const {
                message = '¿Está seguro de realizar esta acción?',
                title = 'Confirmar Acción',
                confirmText = 'Sí, proceder',
                cancelText = 'Cancelar',
                icon = 'warning'
            } = el.dataset;

            const showConfirmationDialog = (callback) => {
                let formIsValid = true;
                if (el.tagName === 'FORM') {
                    if (typeof el.checkValidity === 'function' && !el.checkValidity()) {
                        el.reportValidity();
                        formIsValid = false;
                    }
                }
                if (!formIsValid) {
                    console.warn("Formulario no válido, confirmación cancelada.");
                    return;
                }
                swalAgrow.fire({
                    title: title,
                    text: message,
                    icon: icon,
                    showCancelButton: true,
                    confirmButtonText: confirmText,
                    cancelButtonText: cancelText,
                    reverseButtons: true
                }).then((result) => {
                    if (result.isConfirmed) callback();
                });
            };

            if (el.tagName === 'FORM') {
                el.addEventListener('submit', (event) => {
                    event.preventDefault();
                    showConfirmationDialog(() => { el.submit(); });
                });
            } else if (el.tagName === 'A') {
                el.addEventListener('click', (event) => {
                    event.preventDefault();
                    const linkUrl = el.href;
                    const target = el.target;
                    const {
                        message: linkMessage = '¿Está seguro de continuar?',
                        title: linkTitle = 'Confirmar Navegación',
                        confirmText: linkConfirm = 'Sí',
                        cancelText: linkCancel = 'No',
                        icon: linkIcon = 'question'
                    } = el.dataset;

                    if (linkUrl) {
                        swalAgrow.fire({
                            title: linkTitle,
                            text: linkMessage,
                            icon: linkIcon,
                            showCancelButton: true,
                            confirmButtonText: linkConfirm,
                            cancelButtonText: linkCancel,
                            reverseButtons: true
                        }).then((result) => {
                            if (result.isConfirmed) {
                                if (target === '_blank') window.open(linkUrl, '_blank');
                                else window.location.href = linkUrl;
                            }
                        });
                    }
                });
            }
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

        fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(`HTTP error ${response.status}: ${text || response.statusText}`); });
                }
                return response.text();
            })
            .then(html => {
                contentDiv.innerHTML = html;
                contentDiv.style.opacity = '1';
                formatPrices(); // Reaplicar formato después de AJAX
                attachConfirmHandlers(); // Reaplicar confirmación
                initFlatpickr(); // Reinicializar datepickers
                attachDynamicEventListeners(); // Volver a adjuntar listeners dinámicos
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

    // --- Funciones de Paginación para Cosechas ---
    window.pageHarvests = function(link) {
        if (!link) return;
        const page = link.getAttribute('data-page');
        const formData = new FormData(document.getElementById('filter-form-harvest') || new FormData());
        formData.set('page', page);
        handleAjaxRequest(`/harvests/table?${new URLSearchParams(formData).toString()}`, 'harvest-list-content');
    };

    // --- Eventos Dinámicos para Contenido AJAX de Cosechas ---
    function attachDynamicEventListeners() {
        // Manejo de formularios de filtro
        document.body.addEventListener('submit', function(event) {
            const form = event.target.closest('form.filter-section');
            if (!form || form.id !== 'filter-form-harvest') return;

            // Prevenir el envío normal del formulario
            event.preventDefault();
            
            const formData = new FormData(form);
            formData.set('page', '0'); // Al filtrar, siempre ir a la página 0
            
            // Ejecutar la petición AJAX
            handleAjaxRequest(`/harvests/table?${new URLSearchParams(formData).toString()}`, 'harvest-list-content');
        });

        // Manejo de paginación
        document.body.addEventListener('click', function(event) {
            const pgLink = event.target.closest('.pagination a[data-page]');
            if (pgLink) {
                event.preventDefault();
                const container = pgLink.closest('[id="harvest-list-content"]');
                if (container) {
                    window.pageHarvests(pgLink);
                }
            }
        });
    }
    attachDynamicEventListeners();

    // Manejo de mensajes de éxito o error
    const flash = document.getElementById('swal-message');
    if (flash) {
        if (flash.dataset.mensaje) {
            swalAgrow.fire({
                title: '¡Éxito!',
                text: flash.dataset.mensaje,
                icon: 'success',
                timer: 2500,
                timerProgressBar: true
            });
        }
        if (flash.dataset.error) {
            swalAgrow.fire({
                title: 'Error',
                text: flash.dataset.error,
                icon: 'error'
            });
        }
    }
});
