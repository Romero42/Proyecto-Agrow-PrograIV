document.addEventListener('DOMContentLoaded', () => {
    // --- Funciones Específicas de Venta ---
    function calculateAndDisplayTotalSale() {
        const quantityInput = document.getElementById('quantitySold');
        const priceInput = document.getElementById('pricePerUnitSold');
        const totalDisplay = document.getElementById('totalSaleDisplay');
        if (!quantityInput || !priceInput || !totalDisplay) return;

        const quantity = parseInt(quantityInput.value, 10);
        const price = parseFloat(priceInput.value);

        if (!isNaN(quantity) && quantity > 0 && !isNaN(price) && price > 0) {
            const total = quantity * price;
            totalDisplay.textContent = `Total Venta: ${formatColones(total)}`; // formatColones from default.js
            totalDisplay.style.display = 'block';
        } else {
            totalDisplay.textContent = '';
            totalDisplay.style.display = 'none';
        }
    }

    function validateQuantityInput() {
        const quantityInput = document.getElementById('quantitySold');
        const quantityError = document.getElementById('quantityError');
        if (!quantityInput || !quantityError) return true; // No hay elementos para validar

        const maxAttr = quantityInput.getAttribute('max');
        const max = maxAttr ? parseInt(maxAttr, 10) : null;
        const current = parseInt(quantityInput.value, 10);

        let isValid = true;
        quantityError.style.display = 'none'; // Ocultar error por defecto
        quantityInput.classList.remove('input-error');

        if (max !== null && !isNaN(current) && current > max) {
            quantityError.textContent = `La cantidad no puede exceder el máximo (${max} kg).`;
            isValid = false;
        } else if (!isNaN(current) && current <= 0) {
            quantityError.textContent = 'La cantidad debe ser mayor a cero.';
            isValid = false;
        } else if (isNaN(current) && quantityInput.value.trim() !== '') { // Si no es un número y no está vacío
            quantityError.textContent = 'Ingrese un número válido.';
            isValid = false;
        }

        if (!isValid) {
            quantityError.style.display = 'block';
            quantityInput.classList.add('input-error');
        }
        return isValid;
    }

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
                // No cambiar el valor automáticamente si excede, solo validar
            } else {
                quantityInput.max = 0;
                quantityInput.placeholder = 'No disponible';
                quantityInput.value = '';
                quantityInput.disabled = true;
            }
        } else { // Si no hay cosecha seleccionada
            quantityInput.removeAttribute('max');
            quantityInput.placeholder = 'Seleccione cosecha primero';
            quantityInput.value = '';
            quantityInput.disabled = true;
        }
        validateQuantityInput(); // Revalidar después de cambiar max
        calculateAndDisplayTotalSale();
    }

    // --- Inicialización y Listeners Específicos de Venta ---

    function initSaleFormDatePickers() {
        const saleDateInputs = document.querySelectorAll('#saleForm .date-picker-dmy, #editSaleForm .date-picker-dmy');
        saleDateInputs.forEach(input => {
            if (input._flatpickr) return; // Ya inicializado
            flatpickr(input, {
                dateFormat: 'd/m/Y',
                allowInput: true,
                maxDate: 'today'
            });
        });
    }
    initSaleFormDatePickers();

    const quantitySoldInput = document.getElementById('quantitySold');
    const pricePerUnitSoldInput = document.getElementById('pricePerUnitSold');

    if (quantitySoldInput) {
        quantitySoldInput.addEventListener('input', () => {
            validateQuantityInput();
            calculateAndDisplayTotalSale();
        });
    }
    if (pricePerUnitSoldInput) {
        pricePerUnitSoldInput.addEventListener('input', calculateAndDisplayTotalSale);
    }

    const harvestSelectForCreate = document.getElementById('harvestId');
    if (harvestSelectForCreate && document.getElementById('saleForm')) {
        harvestSelectForCreate.addEventListener('change', updateMaxQuantityAndPrice);
        if (harvestSelectForCreate.value) {
            updateMaxQuantityAndPrice();
        }
    }

    // Para el formulario de edición, inicializar validación y cálculo
    if (document.getElementById('editSaleForm')) {
        if (quantitySoldInput) validateQuantityInput(); // Validar al cargar la página de edición
        calculateAndDisplayTotalSale(); // Calcular total al cargar
    }

    // --- Manejo de Submit y Confirmación para Formularios de Venta ---

    function handleSaleFormSubmit(event) {
        const form = event.target;
        event.preventDefault(); // Siempre prevenir el envío por defecto primero

        // Validación HTML5 básica
        if (typeof form.checkValidity === 'function' && !form.checkValidity()) {
            form.reportValidity();
            return;
        }
        // Validación específica de cantidad (asegura que esté dentro del stock, etc.)
        if (!validateQuantityInput()) {
            swalAgrow.fire('Error de Validación', 'Por favor, corrija la cantidad vendida.', 'error');
            return;
        }
        // Otras validaciones JS si son necesarias antes de SweetAlert

        const {
            message = '¿Está seguro de realizar esta acción?',
            title = 'Confirmar Acción',
            confirmText = 'Sí, proceder',
            cancelText = 'Cancelar',
            icon = 'warning'
        } = form.dataset; // Tomar datos del dataset del form

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
                form.submit(); // Solo enviar si todo es válido y confirmado
            }
        });
    }

    const saleForm = document.getElementById('saleForm');
    if (saleForm) {
        saleForm.addEventListener('submit', handleSaleFormSubmit);
    }

    const editSaleForm = document.getElementById('editSaleForm');
    if (editSaleForm) {
        editSaleForm.addEventListener('submit', handleSaleFormSubmit);
    }

    const salesModuleConfig = {
        filterFormId: 'filter-form-sales',      // ID del formulario de filtro
        tableContentId: 'sales-list-content',   // ID del div que contiene la tabla
        defaultPaginateUrl: '/sales/table',     // URL para paginación sin filtros (o la base)
        ajaxSuccessCallback: function() {       // Callback después de cargar AJAX
            console.log("Tabla de ventas recargada vía AJAX.");
            initSaleFormDatePickers(); // Re-inicializar flatpickr para filtros si es necesario
            // Si el filtro tiene un campo de fecha `saleDateFilter`
            const saleDateFilterInput = document.getElementById('saleDateFilter');
            const saleDateHiddenInput = document.getElementById('saleDate');
            if (saleDateFilterInput && saleDateHiddenInput && !saleDateFilterInput._flatpickr) {
                 flatpickr(saleDateFilterInput, {
                    altInput: true,
                    altFormat: "d/m/Y",
                    dateFormat: "Y-m-d",
                    allowInput: true,
                    maxDate: 'today',
                    onReady: function(selectedDates, dateStr, instance) {
                        if (saleDateHiddenInput.value) {
                            instance.setDate(saleDateHiddenInput.value, false);
                        }
                    },
                    onChange: function(selectedDates, dateStr, instance) {
                        saleDateHiddenInput.value = dateStr;
                    }
                });
            }
        }
    };

    if (document.getElementById(salesModuleConfig.tableContentId)) { // Solo si estamos en la página de listado
        if (typeof attachGenericTableListeners === 'function') {
            attachGenericTableListeners(salesModuleConfig);
        } else {
            console.error("attachGenericTableListeners no está definida. Asegúrate que default.js se carga antes.");
        }
        // Llamada inicial para filtros si hay un campo de fecha en el listado
        salesModuleConfig.ajaxSuccessCallback();
    }
});