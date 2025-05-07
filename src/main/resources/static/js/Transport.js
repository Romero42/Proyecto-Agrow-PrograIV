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

    // --- Confirmación antes de enviar ---
    function handleTransportFormSubmit(event) {
        event.preventDefault(); // Evitar el envío inmediato

        // Primero validamos la cantidad
        if (!validateQuantityInput()) {
            Swal.fire({
                title: 'Error de Validación',
                text: 'Por favor, corrija la cantidad transportada.',
                icon: 'error',
                confirmButtonText: 'Entendido'
            });
            return;
        }

        // Mostramos confirmación antes de enviar
        Swal.fire({
            title: '¿Está seguro de realizar los cambios?',
            text: 'Esta acción no se puede deshacer.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí, guardar cambios',
            cancelButtonText: 'Cancelar',
            reverseButtons: true
        }).then((result) => {
            if (result.isConfirmed) {
                // Si confirma, mostramos mensaje de éxito y luego enviamos el formulario
                Swal.fire({
                    title: 'ÉXITO',
                    text: 'Los cambios se han guardado correctamente.',
                    icon: 'success',
                    confirmButtonText: 'Aceptar'
                }).then(() => {
                    event.target.submit(); // Envío del formulario después de confirmar
                });
            }
        });
    }

    const editTransportForm = document.getElementById('editTransportForm');
    if (editTransportForm) {
        editTransportForm.addEventListener('submit', handleTransportFormSubmit);
    }

    // Listener para calcular automáticamente al modificar valores
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
});