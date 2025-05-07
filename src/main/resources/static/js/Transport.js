document.addEventListener('DOMContentLoaded', () => {
    // --- Funciones Específicas de Transporte ---
    function calculateAndDisplayTotalTransport() {
        const quantityInput = document.getElementById('quantityTransported');
        const priceInput = document.getElementById('pricePerUnitTransport');
        const totalDisplay = document.getElementById('totalTransportDisplay');
        if (!quantityInput || !priceInput || !totalDisplay) return;

        const quantity = parseInt(quantityInput.value, 10);
        const price = parseFloat(priceInput.value);

        if (!isNaN(quantity) && quantity > 0 && !isNaN(price) && price > 0) {
            const total = quantity * price;
            totalDisplay.textContent = `Total Transporte: ${formatColones(total)}`;
            totalDisplay.style.display = 'block';
        } else {
            totalDisplay.textContent = '';
            totalDisplay.style.display = 'none';
        }
    }

    function validateQuantityInput() {
        const quantityInput = document.getElementById('quantityTransported');
        const quantityError = document.getElementById('quantityError');
        if (!quantityInput || !quantityError) return true;

        const maxAttr = quantityInput.getAttribute('max');
        const max = maxAttr ? parseInt(maxAttr, 10) : null;
        const current = parseInt(quantityInput.value, 10);

        let isValid = true;
        quantityError.style.display = 'none';
        quantityInput.classList.remove('input-error');

        if (max !== null && !isNaN(current) && current > max) {
            quantityError.textContent = `La cantidad no puede exceder el máximo (${max}).`;
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

    function updateMaxQuantityAndPrice() {
        const transportSelect = document.getElementById('transportId');
        const quantityInput = document.getElementById('quantityTransported');
        if (!transportSelect || !quantityInput) return;

        const selectedOption = transportSelect.options[transportSelect.selectedIndex];

        if (selectedOption && selectedOption.value) {
            const available = parseInt(selectedOption.getAttribute('data-available'), 10);

            if (!isNaN(available) && available > 0) {
                quantityInput.max = available;
                quantityInput.placeholder = `Máximo disponible: ${available}`;
                quantityInput.disabled = false;
            } else {
                quantityInput.max = 0;
                quantityInput.placeholder = 'No disponible';
                quantityInput.value = '';
                quantityInput.disabled = true;
            }
        } else {
            quantityInput.removeAttribute('max');
            quantityInput.placeholder = 'Seleccione transporte primero';
            quantityInput.value = '';
            quantityInput.disabled = true;
        }
        validateQuantityInput();
        calculateAndDisplayTotalTransport();
    }

    // --- Inicialización y Listeners Específicos de Transporte ---
    function initTransportFormDatePickers() {
        const transportDateInputs = document.querySelectorAll('#transportForm .date-picker-dmy, #editTransportForm .date-picker-dmy');
        transportDateInputs.forEach(input => {
            if (input._flatpickr) return;
            flatpickr(input, {
                dateFormat: 'd/m/Y',
                allowInput: true,
                maxDate: 'today'
            });
        });
    }
    initTransportFormDatePickers();

    const quantityTransportedInput = document.getElementById('quantityTransported');
    const pricePerUnitTransportInput = document.getElementById('pricePerUnitTransport');

    if (quantityTransportedInput) {
        quantityTransportedInput.addEventListener('input', () => {
            validateQuantityInput();
            calculateAndDisplayTotalTransport();
        });
    }
    if (pricePerUnitTransportInput) {
        pricePerUnitTransportInput.addEventListener('input', calculateAndDisplayTotalTransport);
    }

    const transportSelectForCreate = document.getElementById('transportId');
    if (transportSelectForCreate && document.getElementById('transportForm')) {
        transportSelectForCreate.addEventListener('change', updateMaxQuantityAndPrice);
        if (transportSelectForCreate.value) {
            updateMaxQuantityAndPrice();
        }
    }

    // --- Manejo de Submit y Confirmación para Formularios de Transporte ---
    function handleTransportFormSubmit(event) {
        const form = event.target;
        event.preventDefault();

        if (typeof form.checkValidity === 'function' && !form.checkValidity()) {
            form.reportValidity();
            return;
        }
        if (!validateQuantityInput()) {
            swalAgrow.fire('Error de Validación', 'Por favor, corrija la cantidad transportada.', 'error');
            return;
        }

        const {
            message = '¿Está seguro de realizar esta acción?',
            title = 'Confirmar Acción',
            confirmText = 'Sí, proceder',
            cancelText = 'Cancelar',
            icon = 'warning'
        } = form.dataset;

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
                form.submit();
            }
        });
    }

    const transportForm = document.getElementById('transportForm');
    if (transportForm) {
        transportForm.addEventListener('submit', handleTransportFormSubmit);
    }

    const editTransportForm = document.getElementById('editTransportForm');
    if (editTransportForm) {
        editTransportForm.addEventListener('submit', handleTransportFormSubmit);
    }

    const transportModuleConfig = {
        filterFormId: 'filter-form-transport',
        tableContentId: 'transport-list-content',
        defaultPaginateUrl: '/transport/table',
        ajaxSuccessCallback: function() {
            console.log("Tabla de transportes recargada vía AJAX.");
            initTransportFormDatePickers();
        }
    };

    if (document.getElementById(transportModuleConfig.tableContentId)) {
        if (typeof attachGenericTableListeners === 'function') {
            attachGenericTableListeners(transportModuleConfig);
        } else {
            console.error("attachGenericTableListeners no está definida. Asegúrate que default.js se carga antes.");
        }
        transportModuleConfig.ajaxSuccessCallback();
    }
});
