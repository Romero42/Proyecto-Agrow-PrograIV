// resources/static/js/app.js

document.addEventListener('DOMContentLoaded', () => {

    // Configuración de SweetAlert (sin cambios)
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

    // Inicialización de Flatpickr (sin cambios)
    function initFlatpickr() {
        if (!window.flatpickr) {
            console.warn("La librería Flatpickr no está cargada.");
            return;
        }
        // Selectores de fecha generales
        flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
        flatpickr('.date-picker-future-iso', { dateFormat: 'Y-m-d', allowInput: true, minDate: 'today' });
        flatpickr('.date-picker-dmy', { dateFormat: 'd/m/Y', allowInput: true });

        // Lógica de rango de fechas para alquileres (sin cambios)
        const start = document.querySelector("#rentStartDay");
        const end = document.querySelector("#rentFinalDay");
        if (start && end) {
            try {
                const startPicker = flatpickr(start, { dateFormat: "Y-m-d", allowInput: true });
                const endPicker = flatpickr(end, { dateFormat: "Y-m-d", allowInput: true });

                startPicker.config.onChange.push(function (selectedDates) {
                    if (selectedDates.length) {
                        endPicker.set("minDate", selectedDates[0]);
                    } else {
                        endPicker.set("minDate", null);
                    }
                    if (endPicker.selectedDates.length && selectedDates.length && endPicker.selectedDates[0] < selectedDates[0]) {
                        endPicker.clear();
                    }
                });

                if (startPicker.selectedDates.length) {
                    endPicker.set("minDate", startPicker.selectedDates[0]);
                    if (endPicker.selectedDates.length && endPicker.selectedDates[0] < startPicker.selectedDates[0]) {
                        endPicker.clear();
                    }
                }

            } catch (e) {
                console.error("Error al inicializar pickers de rango:", e);
            }
        }
    }
    initFlatpickr();

    // Formato de moneda (CRC) (sin cambios)
    const formatColones = val => {
        const num = parseFloat(val);
        if (isNaN(num)) return '---';
        return new Intl.NumberFormat('es-CR', {
            style: 'currency',
            currency: 'CRC',
            minimumFractionDigits: 2
        }).format(num);
    };

    // Aplicar formato de precios (sin cambios)
    function formatPrices() {
        document.querySelectorAll('.price-display').forEach(el => {
            const value = el.dataset.value || el.textContent;
            el.textContent = formatColones(value);
        });
    }
    formatPrices();

    // Cálculo y muestra del total de venta (sin cambios)
    function calculateAndDisplayTotalSale() {
        const quantityInput = document.getElementById('quantitySold');
        const priceInput = document.getElementById('pricePerUnitSold');
        const totalDisplay = document.getElementById('totalSaleDisplay');
        if (!quantityInput || !priceInput || !totalDisplay) return;

        const quantity = parseInt(quantityInput.value, 10);
        const price = parseFloat(priceInput.value);

        if (!isNaN(quantity) && quantity > 0 && !isNaN(price) && price > 0) {
            const total = quantity * price;
            totalDisplay.textContent = `Total Venta: ${formatColones(total)}`;
            totalDisplay.style.display = 'block';
        } else {
            totalDisplay.textContent = '';
            totalDisplay.style.display = 'none';
        }
    }

    // Validación de cantidad de venta (sin cambios)
    function validateQuantityInput() {
        const quantityInput = document.getElementById('quantitySold');
        const quantityError = document.getElementById('quantityError');
        if (!quantityInput || !quantityError) return true;

        const maxAttr = quantityInput.getAttribute('max');
        const max = maxAttr ? parseInt(maxAttr, 10) : null;
        const current = parseInt(quantityInput.value, 10);

        let isValid = true;
        quantityError.style.display = 'none';
        quantityInput.classList.remove('input-error');

        if (max !== null && !isNaN(current) && current > max) {
            quantityError.textContent = `La cantidad no puede exceder el máximo (${max} kg).`;
            isValid = false;
        } else if (!isNaN(current) && current <= 0) {
            quantityError.textContent = 'La cantidad debe ser mayor a cero.';
            isValid = false;
        } else if (isNaN(current) && quantityInput.value.trim() !== '') {
            quantityError.textContent = 'Ingrese un número válido.';
            isValid = false;
        }

        if (!isValid) {
            quantityError.style.display = 'block';
            quantityInput.classList.add('input-error');
        }
        return isValid;
    }

    // Actualizar cantidad máxima y placeholder de precio (sin cambios)
    function updateMaxQuantityAndPrice() {
        const harvestSelect = document.getElementById('harvestId');
        const quantityInput = document.getElementById('quantitySold');
        if (!harvestSelect || !quantityInput) return;

        const selectedOption = harvestSelect.options[harvestSelect.selectedIndex];

        if (selectedOption && selectedOption.value) {
            const available = parseInt(selectedOption.getAttribute('data-available'), 10);

            if (!isNaN(available) && available > 0) {
                quantityInput.max = available;
                quantityInput.placeholder = `Máximo disponible: ${available} kg`;
                quantityInput.disabled = false;
                if (parseInt(quantityInput.value, 10) > available) {
                    quantityInput.value = available;
                }
                validateQuantityInput();
            } else {
                quantityInput.max = 0;
                quantityInput.placeholder = 'No disponible';
                quantityInput.value = '';
                quantityInput.disabled = true;
                validateQuantityInput();
            }
        } else {
            quantityInput.removeAttribute('max');
            quantityInput.placeholder = 'Seleccione cosecha primero';
            quantityInput.value = '';
            quantityInput.disabled = true;
            validateQuantityInput();
        }
        calculateAndDisplayTotalSale();
    }

    // Listeners para inputs de venta (sin cambios)
    document.body.addEventListener('input', (event) => {
        if (event.target.matches('#quantitySold') || event.target.matches('#pricePerUnitSold')) {
            calculateAndDisplayTotalSale();
        }
        if (event.target.matches('#quantitySold')) {
            validateQuantityInput();
        }
    });

    const harvestSelectForCreate = document.getElementById('harvestId');
    if (harvestSelectForCreate && document.getElementById('saleForm')) {
        harvestSelectForCreate.addEventListener('change', updateMaxQuantityAndPrice);
        updateMaxQuantityAndPrice();
    }

    if (document.getElementById('editSaleForm')) {
        validateQuantityInput();
        calculateAndDisplayTotalSale();
    }

    // Manejo de diálogos de confirmación (sin cambios)
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
                    if ((el.id === 'saleForm' || el.id === 'editSaleForm') && !validateQuantityInput()) {
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

    // Petición AJAX genérica (sin cambios)
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
                // Recalcular total si es un formulario de venta
                if (document.getElementById('editSaleForm') || document.getElementById('saleForm')) {
                     validateQuantityInput(); // Revalidar
                     calculateAndDisplayTotalSale(); // Recalcular
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

    // --- Funciones de Paginación ---

    // Paginación para Cosechas (sin cambios)
    window.pageHarvests = function(link) {
        if (!link) return;
        const page = link.getAttribute('data-page');
        const formData = new FormData(document.getElementById('filter-form-harvest') || new FormData());
        formData.set('page', page);
        handleAjaxRequest(`/harvests/table?${new URLSearchParams(formData).toString()}`, 'harvest-list-content');
    };

    // Paginación para Suministros (sin cambios)
    window.pageSupplies = function(link) {
        if (!link) return;
        const page = link.getAttribute('data-page');
        const formData = new FormData(document.getElementById('filter-form-supply') || new FormData());
        formData.set('page', page);
        handleAjaxRequest(`/supplies/table?${new URLSearchParams(formData).toString()}`, 'supply-list-content');
    };



    window.pageRequests = function(link) {
        if (!link) return;
        const page = link.getAttribute('data-page');
        // Usa el formulario de filtro si existe, si no, crea uno vacío para añadir 'page'
        const form = document.getElementById('filter-form-request');
        const formData = form ? new FormData(form) : new FormData();
        // Siempre establece la página solicitada
        formData.set('page', page); // Asegúrate de que el controlador maneje 'page'

        // Llama a handleAjaxRequest con la URL del fragmento y el ID del contenedor
        handleAjaxRequest(
            `/requests/table?${new URLSearchParams(formData).toString()}`, // URL para obtener la tabla
            'request-list-content' // ID del div que contiene la tabla
        );
    };
    // *** FIN: Función de paginación para Requests ***


    // --- Eventos Dinámicos para Contenido AJAX ---
    function attachDynamicEventListeners() {
        document.body.addEventListener('submit', function(event) {
            const form = event.target.closest('form.filter-section');
            if (!form) return;

            // Prevenir el envío normal del formulario
            event.preventDefault();

            let contentId, actionUrl;

            // Determinar el contenedor y la URL según el ID del formulario
            if (form.id === 'filter-form-supply') {
                contentId = 'supply-list-content'; actionUrl = '/supplies/table';
            } else if (form.id === 'filter-form-harvest') {
                contentId = 'harvest-list-content'; actionUrl = '/harvests/table';
            } else if (form.id === 'filter-form-producer') {
                contentId = 'table-producer'; actionUrl = '/producers/list';
            } else if (form.id === 'filter-form-rent') {


            } else if (form.id === 'filter-form-request') {
                contentId = 'request-list-content'; // ID del div que contiene la tabla de requests
                actionUrl = '/requests/table';      // URL que devuelve el fragmento de la tabla

            } else {
                console.warn('Formulario de filtro con ID no reconocido:', form.id);
                return; // No hacer nada si el ID no coincide
            }

            const formData = new FormData(form);
            formData.set('page', '0'); // Al filtrar, siempre ir a la página 0

            // Lógica específica para Rent (sin cambios)
            if (form.id === 'filter-form-rent') {
                const s = formData.get('rentStartDay'), e = formData.get('rentFinalDay'), m = formData.get('id_maquina');
                if ((s && m) || (e && m) || (s && e && m)) { swalAgrow.fire("Error", "Use solo un filtro a la vez.", "error"); return; }
                if (s && e && s > e) { swalAgrow.fire("Error", "Fecha inicio > fecha final.", "error"); return; }
                if (!s && !e && !m) { swalAgrow.fire("Info", "Ingrese un filtro.", "info"); return; }
            }

            // Ejecutar la petición AJAX
            handleAjaxRequest(`${actionUrl}?${new URLSearchParams(formData).toString()}`, contentId);
        });

        // Listener para clicks en la paginación (sin cambios, debería funcionar si el container.id se detecta bien)
        document.body.addEventListener('click', function(event) {
            const pgLink = event.target.closest('.pagination a[data-page]');
            if (pgLink) {
                event.preventDefault();
                const container = pgLink.closest('[id$="-list-content"], [id="table-producer"], [id="tableData"]');
                if (container) {
                    if (container.id === 'harvest-list-content') window.pageHarvests(pgLink);
                    else if (container.id === 'supply-list-content') window.pageSupplies(pgLink);
                    else if (container.id === 'table-producer') window.producer(pgLink);


                    else if (container.id === 'request-list-content') window.pageRequests(pgLink); // Llama a la función de paginación de requests

                }
            }
        });
    }
    attachDynamicEventListeners(); // Adjuntar listeners al cargar la página

    // Manejo de mensajes
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

}); // Fin de DOMContentLoaded





