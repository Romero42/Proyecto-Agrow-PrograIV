// Configuración de SweetAlert
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

// Inicialización de Flatpickr
function initFlatpickrGeneral() {
    if (!window.flatpickr) {
        console.warn("La librería Flatpickr no está cargada.");
        return;
    }
    flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
    flatpickr('.date-picker-future-iso', { dateFormat: 'Y-m-d', allowInput: true, minDate: 'today' });
    flatpickr('.date-picker-dmy', { dateFormat: 'd/m/y', allowInput: true, maxDate: 'today' });


    const startPickerElem = document.querySelector("#rentStartDay");
    const endPickerElem = document.querySelector("#rentFinalDay");
    if (startPickerElem && endPickerElem) {
        try {
            const fpStart = flatpickr(startPickerElem, { dateFormat: "Y-m-d", allowInput: true });
            const fpEnd = flatpickr(endPickerElem, { dateFormat: "Y-m-d", allowInput: true });

            fpStart.config.onChange.push(function (selectedDates) {
                if (selectedDates.length) fpEnd.set("minDate", selectedDates[0]);
                else fpEnd.set("minDate", null);
                if (fpEnd.selectedDates.length && selectedDates.length && fpEnd.selectedDates[0] < selectedDates[0]) {
                    fpEnd.clear();
                }
            });
             if (fpStart.selectedDates.length) {
                fpEnd.set("minDate", fpStart.selectedDates[0]);
                 if (fpEnd.selectedDates.length && fpEnd.selectedDates[0] < fpStart.selectedDates[0]) {
                     fpEnd.clear();
                 }
            }
        } catch (e) {
            console.error("Error al inicializar pickers de rango:", e);
        }
    }
}


// Formato de moneda (CRC)
const formatColones = val => {
    const num = parseFloat(val);
    if (isNaN(num)) return '---';
    return new Intl.NumberFormat('es-CR', {
        style: 'currency',
        currency: 'CRC',
        minimumFractionDigits: 2
    }).format(num);
};

// Aplicar formato de precios
function formatPrices() {
    document.querySelectorAll('.price-display').forEach(el => {
        const value = el.dataset.value || el.textContent;
        el.textContent = formatColones(value);
    });
}

// Manejo de diálogos de confirmación genéricos
function attachConfirmHandlers() {
    document.querySelectorAll('.confirm-action').forEach(el => {
        if (el.dataset.confirmAttached === 'true') return;
        el.dataset.confirmAttached = 'true';

        const {
            message = '¿Está seguro de realizar esta acción?',
            title = 'Confirmar Acción',
            confirmText = 'Sí, proceder',
            cancelText = 'Cancelar',
            icon = 'warning'
        } = el.dataset;

        const showConfirmationDialog = (callback) => {
            // Validación HTML básica del formulario
            if (el.tagName === 'FORM' && typeof el.checkValidity === 'function' && !el.checkValidity()) {
                el.reportValidity(); // Muestra los mensajes de validación del navegador
                console.warn("Formulario HTML5 no válido, confirmación cancelada.");
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

            el.addEventListener('submit', function (event) {

                if (el.id === 'saleForm' || el.id === 'editSaleForm') {
                    // Sale.js se encarga de esto. No hacer nada aquí para evitar doble diálogo.
                    return;
                }

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


// Petición AJAX genérica
function handleAjaxRequest(url, contentDivId, successCallback) {
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

            formatPrices();
            attachConfirmHandlers();
            initFlatpickrGeneral();

            // Ejecutar callback específico del módulo si se proporcionó
            if (typeof successCallback === 'function') {
                successCallback();
            }
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

// Manejo de mensajes flash
function displayFlashMessages() {
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
}

function attachGenericTableListeners(moduleConfig) {
    const filterForm = document.getElementById(moduleConfig.filterFormId);
    if (filterForm) {
        filterForm.addEventListener('submit', function(event) {
            event.preventDefault(); // Prevenir el envío normal del formulario

            const formData = new FormData(filterForm);
            formData.set('page', '0'); // Al filtrar, siempre ir a la página 0

            const actionUrl = filterForm.getAttribute('action') || moduleConfig.defaultPaginateUrl;
            const params = new URLSearchParams(formData).toString();
            const url = actionUrl + (actionUrl.includes('?') ? '&' : '?') + params;

            handleAjaxRequest(
                url,
                moduleConfig.tableContentId,
                moduleConfig.ajaxSuccessCallback
            );
        });
    }

    // Listener para clicks en la paginación
    document.body.addEventListener('click', function(event) {
        const pgLink = event.target.closest(
            `#${moduleConfig.tableContentId} .pagination a[data-page]`
        );
        if (!pgLink) return;

        event.preventDefault();
        const page = pgLink.getAttribute('data-page');
        const currentFilterForm = document.getElementById(moduleConfig.filterFormId);
        const formData = currentFilterForm ? new FormData(currentFilterForm) : new FormData();
        formData.set('page', page);

        // URL base (del form o la default)
        const baseUrl = (currentFilterForm && currentFilterForm.getAttribute('action'))
            ? currentFilterForm.getAttribute('action')
            : moduleConfig.defaultPaginateUrl;

        if (!baseUrl) {
            console.error("No se pudo determinar la URL de paginación.");
            return;
        }

        const params = new URLSearchParams(formData).toString();
        const url = baseUrl + (baseUrl.includes('?') ? '&' : '?') + params;

        handleAjaxRequest(
            url,
            moduleConfig.tableContentId,
            moduleConfig.ajaxSuccessCallback
        );
    });
}



// Ejecutar al cargar el DOM
document.addEventListener('DOMContentLoaded', () => {
    initFlatpickrGeneral();
    formatPrices();
    attachConfirmHandlers();
    displayFlashMessages();
});