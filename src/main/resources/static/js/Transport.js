document.addEventListener('DOMContentLoaded', () => {
    // ================================
    // === LÓGICA ESPECÍFICA: TRANSPORTE
    // ================================

    function calculateAndDisplayTotalTransport() {
        const quantityInput = document.getElementById('quantityTransported');
        const priceInput = document.getElementById('pricePerUnitTransport');
        const totalDisplay = document.getElementById('totalTransportDisplay');
        if (!quantityInput || !priceInput || !totalDisplay)
            return;

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

    function formatColones(value) {
        return value.toLocaleString('es-CR', {style: 'currency', currency: 'CRC'});
    }

    function validateQuantityInput() {
        const quantityInput = document.getElementById('quantityTransported');
        const quantityError = document.getElementById('quantityError');
        if (!quantityInput || !quantityError)
            return true;

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

    // Validación y cálculo al cambiar cantidad o precio
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

    // =======================================
    // === FUNCIÓN GENÉRICA DE CONFIRMACIÓN
    // =======================================

    const confirmableForms = document.querySelectorAll('form.confirm-action');

    confirmableForms.forEach(form => {
        form.addEventListener('submit', (event) => {
            event.preventDefault(); // Detener envío inmediato

            const title = form.dataset.title || '¿Confirmar acción?';
            const message = form.dataset.message || '¿Estás seguro de realizar esta acción?';

            // Si hay un validador específico para este formulario
            if (form.id === 'editTransportForm' && !validateQuantityInput()) {
                Swal.fire({
                    title: 'Error de Validación',
                    text: 'Por favor, corrija la cantidad transportada.',
                    icon: 'error',
                    confirmButtonText: 'Entendido'
                });
                return;
            }

            Swal.fire({
                title: title,
                text: message,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Sí, confirmar',
                cancelButtonText: 'Cancelar',
                reverseButtons: true
            }).then(result => {
                if (result.isConfirmed) {
                    const isDeleteForm = form.action.includes('/transport/delete');

                    const successText = isDeleteForm
                            ? 'El transporte ha sido eliminado correctamente.'
                            : 'Los cambios se han guardado correctamente.';

                    Swal.fire({
                        title: 'ÉXITO',
                        text: successText,
                        icon: 'success',
                        confirmButtonText: 'Aceptar'
                    }).then(() => {
                        form.submit();
                    });
                }
            });

        });
    });
});
