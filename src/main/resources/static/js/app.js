document.addEventListener('DOMContentLoaded', () => {

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

function initFlatpickr() {

    if (!window.flatpickr) {
        console.warn("Flatpickr library not loaded.");
        return;
    }
    // Selectores generales para fechas pasadas y futuras
    flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
    flatpickr('.date-picker-future-iso', { dateFormat: 'Y-m-d', allowInput: true, minDate: 'today' });

    // Selectores específicos para rangos si existen
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
            console.error("Error initializing date range pickers:", e);
        }
    }
}

initFlatpickr();

    const formatColones = val => {
        const num = parseFloat(val);
        if (isNaN(num)) return '---';
        return new Intl.NumberFormat('es-CR', {
    style: 'currency',
            currency: 'CRC',
            minimumFractionDigits: 2
}).format(num);
    };

function formatPrices() {
    document.querySelectorAll('.price-display').forEach(el => {
            const value = el.dataset.value || el.textContent;
    el.textContent = formatColones(value);
        });
}

formatPrices();

// Función para calcular y mostrar total
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

// Listener para inputs de cantidad y precio en cualquier form que los tenga
    document.body.addEventListener('input', (event) => {
        if (event.target.matches('#quantitySold') || event.target.matches('#pricePerUnitSold')) {
calculateAndDisplayTotalSale();
        }
                if (event.target.matches('#quantitySold')) {
validateQuantityInput();
         }
                 });


// Función para validar cantidad
function validateQuantityInput() {
        const quantityInput = document.getElementById('quantitySold');
        const quantityError = document.getElementById('quantityError');
    if (!quantityInput || !quantityError) return;

        const maxAttr = quantityInput.getAttribute('max');
        const max = maxAttr ? parseInt(maxAttr, 10) : null;
        const current = parseInt(quantityInput.value, 10);

    if (max !== null && !isNaN(current) && current > max) {
        quantityError.textContent = `La cantidad no puede exceder el máximo (${max} kg).`; // Mensaje dinámico
        quantityError.style.display = 'block';
        quantityInput.classList.add('input-error');
        return false; // Indica error
    } else if (max !== null && !isNaN(current) && current <= 0){
        quantityError.textContent = 'La cantidad debe ser mayor a cero.';
        quantityError.style.display = 'block';
        quantityInput.classList.add('input-error');
        return false; // Indica error
    } else {
        quantityError.style.display = 'none';
        quantityInput.classList.remove('input-error');
        return true; // Indica que está bien
    }
}

// --- Lógica para selección de cosecha y actualización de cantidad ---
function updateMaxQuantityAndPrice() {
        const harvestSelect = document.getElementById('harvestId'); // Presente en form_sale
        const quantityInput = document.getElementById('quantitySold');

    if (!harvestSelect || !quantityInput) return;

        const selectedOption = harvestSelect.options[harvestSelect.selectedIndex];

    if (selectedOption && selectedOption.value) {
            const available = parseInt(selectedOption.getAttribute('data-available'), 10);

        if (!isNaN(available) && available > 0) {
            quantityInput.max = available;
            quantityInput.placeholder = `Máximo disponible: ${available} kg`;
            quantityInput.disabled = false;

            // Ajustar valor si excede el máximo al cambiar de cosecha
            if (parseInt(quantityInput.value, 10) > available) {
                quantityInput.value = available;
            }

            // Forzar validación visual al cambiar
            validateQuantityInput();

        } else {
            quantityInput.max = 0;
            quantityInput.placeholder = 'No disponible';
            quantityInput.value = '';
            quantityInput.disabled = true;
            validateQuantityInput();
        }
    } else {
        // Si no hay cosecha seleccionada
        quantityInput.removeAttribute('max'); // Quitar restricción max
        quantityInput.placeholder = 'Seleccione cosecha primero';
        quantityInput.value = '';
        quantityInput.disabled = true;
        validateQuantityInput(); // Limpiar error visual
    }
    calculateAndDisplayTotalSale(); // Recalcular total
}

// Event listener para el select de cosecha en form_sale.html
    const harvestSelectForCreate = document.getElementById('harvestId');
    if (harvestSelectForCreate && document.getElementById('saleForm')) {
        harvestSelectForCreate.addEventListener('change', updateMaxQuantityAndPrice);
// Llamar una vez al cargar por si hay preselección en form_sale
updateMaxQuantityAndPrice();
    }

            // Llamar a la validación y cálculo iniciales en la página de edición
            if (document.getElementById('editSaleForm')) {
validateQuantityInput();
calculateAndDisplayTotalSale();
     }


// --- Manejo de Confirmaciones ---
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
            // Validar campos requeridos ANTES de mostrar el diálogo si es un formulario
            let formIsValid = true;
    if (el.tagName === 'FORM') {
        if (typeof el.checkValidity === 'function' && !el.checkValidity()) {
            // Mostrar mensajes de validación HTML5 nativos
            el.reportValidity();
            formIsValid = false;
        }

        if (el.id === 'saleForm' || el.id === 'editSaleForm') {
            if (!validateQuantityInput()) { // Re-valida cantidad
                formIsValid = false;
            }

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
    if (result.isConfirmed) {
        callback();
    }
                });
            };

    if (el.tagName === 'FORM') {
        el.addEventListener('submit', (event) => {
                event.preventDefault();
        showConfirmationDialog(() => {
                el.submit();
                    });
                });
    } else if (el.tagName === 'A') {

el.addEventListener('click', (event) => {
    event.preventDefault();
    const linkUrl = el.href;
    const target = el.target;

    const {
        message = '¿Está seguro de realizar esta acción?',
        title = 'Confirmar Acción',
        confirmText = 'Sí, proceder',
        cancelText = 'Cancelar',
        icon = 'warning'
    } = el.dataset;


    if (linkUrl) {

         swalAgrow.fire({
            title: title,
            text: message,
            icon: icon,
            showCancelButton: true,
            confirmButtonText: confirmText,
            cancelButtonText: cancelText,
            reverseButtons: true
        }).then((result) => {
            if (result.isConfirmed) {
                if (target === '_blank') {
                    window.open(linkUrl, '_blank');
                } else {
                    window.location.href = linkUrl;
                }
            }
        });
    }
});
    } else if (el.tagName === 'BUTTON' && el.type !== 'submit') {
        el.addEventListener('click', (event) => {

        });
    }
        });
}

attachConfirmHandlers();

 // --- Manejo de AJAX para paginación y filtros ---
    function handleAjaxRequest(url, contentDivId) {
        const contentDiv = document.getElementById(contentDivId);
        if (!contentDiv) {
            console.error(`Element with ID '${contentDivId}' not found.`);
            swalAgrow.fire('Error', `Error interno: Contenedor '${contentDivId}' no encontrado.`, 'error');
            return;
        }
        // Mostrar indicador visual de carga
        const loader = document.createElement('div');
        loader.innerHTML = '<div style="text-align:center; padding: 20px;"><span class="material-symbols-outlined" style="font-size: 40px; animation: spin 1.5s linear infinite;">autorenew</span><p>Cargando...</p></div>';
        contentDiv.innerHTML = '';
        contentDiv.appendChild(loader);
        contentDiv.style.opacity = '0.7';


        fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
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
                 formatPrices();
                 attachConfirmHandlers();
                 initFlatpickr();
                 attachDynamicEventListeners();
                 // Llamadas específicas si son necesarias para el contenido nuevo
                 if (document.getElementById('editSaleForm') || document.getElementById('saleForm')) {
                    validateQuantityInput();
                    calculateAndDisplayTotalSale();
                 }
            })
            .catch(error => {
                console.error(`Error fetching content for ${contentDivId}:`, error);
                 let errorMsg = 'No se pudo cargar el contenido solicitado. Verifique la conexión o inténtelo más tarde.';
                 if (error.message.includes('HTTP error')) {
                    errorMsg = `Error al cargar: ${error.message}. Por favor, intente de nuevo.`;
                 }
                 swalAgrow.fire('Error de Carga', errorMsg, 'error');
                contentDiv.innerHTML = `<div class="info-card"><span class="material-symbols-outlined icon">error</span><h2>Error de Carga</h2><p>No se pudieron cargar los datos (${error.message}). Por favor, actualice la página o inténtelo de nuevo.</p></div>`;
                 contentDiv.style.opacity = '1';
            });
    }

// --- Función para paginación AJAX de Cosechas ---
window.pageHarvests = function(link) {
    if (!link) return;
        const page = link.getAttribute('data-page');
        const stateC = link.getAttribute('data-state');
        const destinyC = link.getAttribute('data-destiny');
        const params = new URLSearchParams();
    params.append('page', page);
    if (stateC && stateC !== 'null') params.append('stateC', stateC);
    if (destinyC && destinyC !== 'null') params.append('destinyC', destinyC);
    handleAjaxRequest(`/harvests/page?${params.toString()}`, 'harvest-list-content');
};

// --- Función para paginación AJAX de Suministros ---
window.pageSupplies = function(link) {
    if (!link) return;
           const page = link.getAttribute('data-page');
           const filterForm = document.getElementById('filter-form-supply');
           const formData = filterForm ? new FormData(filterForm) : new FormData();
    formData.set('page', page); // Actualiza la página en los datos del form
    handleAjaxRequest(`/supplies/table?${new URLSearchParams(formData).toString()}`, 'supply-list-content');
};

// --- Función para paginación/filtrado AJAX de Productores ---
window.producer = function(button) {
    if (!button) return;
           const isFilterButton = button.getAttribute('data-button') === '1';
           const page = isFilterButton ? '0' : button.getAttribute('data-page'); // Filtrar va a pág 0, paginar usa data-page

           const filterForm = document.getElementById('filter-form-producer');
           const formData = filterForm ? new FormData(filterForm) : new FormData();
    formData.set('page', page); // Establece la página

    // Lógica de limpieza de filtros si se usa ID
           const idProducerInput = filterForm?.querySelector('#id_producer');
           const citySelect = filterForm?.querySelector('#city');
    if (idProducerInput?.value) { // Si se busca por ID
        formData.delete('city'); // Eliminar filtro de ciudad
        if (citySelect) citySelect.value = ''; // Limpiar visualmente select de ciudad
    } else {
        formData.delete('id_producer'); // Si no hay ID, eliminarlo del form data
        if (idProducerInput) idProducerInput.value = ''; // Limpiar visualmente input ID
    }


    handleAjaxRequest(`/producers/list?${new URLSearchParams(formData).toString()}`, 'table-producer');
};

// --- Función para paginación/filtrado AJAX de Alquileres ---
window.pageRent = function(link) {
    if (!link) return;
             const page = link.getAttribute('data-page');
             const params = new URLSearchParams(getCurrentRentFilters()); // Obtener filtros actuales
    params.set('page', page);
              const endpoint = params.has('rentStartDay') || params.has('rentFinalDay') || params.has('id_maquina')
            ? '/rent/tableFilter'
            : '/rent/pageCurrent';
    handleAjaxRequest(`${endpoint}?${params.toString()}`, 'tableData');
};

window.filterRent = function(button) {
    if (!button) return;
            const page = button.getAttribute('data-page') || '0'; // Usar data-page si existe (paginación con filtro), si no, es botón de filtrar (pág 0)
            const params = new URLSearchParams(getCurrentRentFilters()); // Obtener filtros actuales del form
    params.set('page', page);

    // Validar que no haya filtros vacíos o múltiples (a menos que sea solo paginación)
            const hasFilters = params.has('rentStartDay') || params.has('rentFinalDay') || params.has('id_maquina');
            const isJustPaging = !hasFilters && button.hasAttribute('data-page'); // Es paginación sin filtros

    if (hasFilters) {
        // Validar combinaciones inválidas
                 const start = params.get('rentStartDay');
                 const end = params.get('rentFinalDay');
                 const id = params.get('id_maquina');
        if ((start && id) || (end && id) || (start && end && id)) {
            swalAgrow.fire("Error", "Use solo un tipo de filtro a la vez (rango de fechas o código de máquina).", "error");
            return;
        }
        // Validar fechas si ambas están presentes
        if (start && end && start > end) {
            swalAgrow.fire("Error", "La fecha de inicio no puede ser posterior a la fecha final.", "error");
            return;
        }
        // Validar que al menos un filtro tenga valor si se presionó el botón Filtrar
        if (!isJustPaging && !start && !end && !id) {
            swalAgrow.fire("Info", "Por favor ingrese un filtro para buscar.", "info");
            return;
        }

    } else if (!isJustPaging) {
        // Si se presionó Filtrar pero no hay filtros, informar
        swalAgrow.fire("Info", "Por favor ingrese un filtro para buscar.", "info");
        return;
    }

            const endpoint = hasFilters ? '/rent/tableFilter' : '/rent/pageCurrent';
    handleAjaxRequest(`${endpoint}?${params.toString()}`, 'tableData');
};

// Función auxiliar para obtener filtros actuales de alquileres
function getCurrentRentFilters() {
             const filterForm = document.getElementById('filter-form-rent');
             const params = new URLSearchParams();
    if (!filterForm) return params;

             const rentStartDay = filterForm.querySelector('#rentStartDay')?.value;
             const rentFinalDay = filterForm.querySelector('#rentFinalDay')?.value;
             const idMaquina = filterForm.querySelector('#id_maquina')?.value;

    if (rentStartDay) params.append('rentStartDay', rentStartDay);
    if (rentFinalDay) params.append('rentFinalDay', rentFinalDay);
    if (idMaquina) params.append('id_maquina', idMaquina);
    return params;
}


// --- Funciones para ver/cerrar info máquina (Alquileres) ---
window.viewMaquina = function(link) {
    if (!link) return;
             const machineId = link.getAttribute('data-id');
             const infoDiv = document.getElementById('infoMaquina');
    if (infoDiv && machineId) {
        handleAjaxRequest(`/rent/viewMaquina?id_maquina=${machineId}`, 'infoMaquina');
    }
};
window.cerrarInfoMaquina = function() {
             const infoDiv = document.getElementById('infoMaquina');
    if (infoDiv) {
        infoDiv.innerHTML = ""; // Limpiar contenido
    }
};


// --- Delegación de Eventos (AJAX) ---
function attachDynamicEventListeners() {
    // Listener genérico para botones de submit de formularios de filtro
    document.body.addEventListener('submit', function(event) {
            const form = event.target.closest('form.filter-section form'); // Busca el form de filtro
        if (form) {
            event.preventDefault(); // Prevenir envío normal
                const contentDivId = form.closest('.main-content').querySelector('[id$="-list-content"], [id="table-producer"], [id="tableData"]')?.id;
            if (!contentDivId) {
                console.error("Could not find target content div for filter form:", form.id);
                return;
            }

            let actionUrl = form.getAttribute('action');
            // Ajustar URL para Suministros y Cosechas si es necesario (para que apunten a /table o /page)
            if (contentDivId === 'supply-list-content') actionUrl = '/supplies/table';
            if (contentDivId === 'harvest-list-content') actionUrl = '/harvests/page';
            // Los demás ya apuntan a los endpoints correctos (producers/list, rent/tableFilter)


                const formData = new FormData(form);
            formData.set('page', '0'); // Reset page on filter submit

            // Lógica de limpieza para Productor
            if (form.id === 'filter-form-producer') {
                     const idProducerInput = form.querySelector('#id_producer');
                if (idProducerInput?.value) formData.delete('city');
                     else formData.delete('id_producer');
            }

            // Lógica de validación para Alquiler
            if (form.id === 'filter-form-rent') {
                     const start = formData.get('rentStartDay');
                     const end = formData.get('rentFinalDay');
                     const id = formData.get('id_maquina');
                if ((start && id) || (end && id) || (start && end && id)) { swalAgrow.fire("Error", "Use solo un tipo de filtro a la vez.", "error"); return; }
                if (start && end && start > end) { swalAgrow.fire("Error", "Fecha inicio > fecha final.", "error"); return; }
                if (!start && !end && !id) { swalAgrow.fire("Info", "Ingrese un filtro.", "info"); return; }
                actionUrl = '/rent/tableFilter'; // Asegurar que filtrar va al endpoint correcto
            }


            handleAjaxRequest(`${actionUrl}?${new URLSearchParams(formData).toString()}`, contentDivId);
        }
    });

    // Listener genérico para links de paginación dentro de contenedores AJAX
    document.body.addEventListener('click', function(event) {
             const paginationLink = event.target.closest('.pagination a[data-page]');
        if (paginationLink) {
            event.preventDefault();
                 const contentDiv = paginationLink.closest('[id$="-list-content"], [id="table-producer"], [id="tableData"]');
            if (contentDiv) {
                       const contentDivId = contentDiv.id;
                // Determinar la función de paginación correcta basada en el ID del contenedor
                if (contentDivId === 'harvest-list-content') window.pageHarvests(paginationLink);
                else if (contentDivId === 'supply-list-content') window.pageSupplies(paginationLink);
                else if (contentDivId === 'table-producer') window.producer(paginationLink); // 'producer' maneja paginación y filtro
                else if (contentDivId === 'tableData') window.filterRent(paginationLink); // 'filterRent' maneja paginación con filtro
                else console.warn("No pagination handler found for content ID:", contentDivId);
            }
        }

        // Listener para "Ver Maquina" (si es necesario fuera de tableData)
               const viewMachineButton = event.target.closest('a[data-id][onclick*="viewMaquina"]');
        if (viewMachineButton && !viewMachineButton.closest('.pagination')) {
            event.preventDefault(); // Prevenir si ya está manejado por onclick
            // window.viewMaquina(viewMachineButton); // Ya se llama con onclick
        }

        // Listener para "Cerrar Info Maquina" (si es necesario fuera de infoMaquina)
                const closeInfoButton = event.target.closest('a[onclick*="cerrarInfoMaquina"]');
        if (closeInfoButton) {
            event.preventDefault(); // Prevenir si ya está manejado por onclick
            // window.cerrarInfoMaquina(); // Ya se llama con onclick
        }

    });
}

attachDynamicEventListeners();

// --- Muestra mensajes Flash ---
    const flash = document.getElementById('swal-message');
    if (flash) {
        if (flash.dataset.mensaje) swalAgrow.fire({ title: '¡Éxito!', text: flash.dataset.mensaje, icon: 'success', timer: 2500, timerProgressBar: true });
        if (flash.dataset.error) swalAgrow.fire({ title: 'Error', text: flash.dataset.error, icon: 'error' });
        }

        });