document.addEventListener('DOMContentLoaded', () => {

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
        return value.toLocaleString('es-CR', {style: 'currency', currency: '₡'});
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

    // === VALIDACIONES DE DATOS DE ENTRADA ===

    function validateTransportFormInputs() {
        const nameRegex = /^[A-Za-zÁÉÍÓÚÑáéíóúñ\s]+$/;
        const phoneRegex = /^[0-9]{8}$/;
        const plateRegex = /^[A-Z0-9\-]{5,10}$/i;

        const company = document.getElementById("transport_company").value.trim();
        const driver = document.getElementById("driver_name").value.trim();
        const phone = document.getElementById("driver_phone").value.trim();
        const plate = document.getElementById("vehicle_plate").value.trim();
        const cost = document.getElementById("transport_cost").value;

        let isValid = true;
        let messages = [];

        if (!nameRegex.test(company)) {
            isValid = false;
            messages.push("La empresa solo debe contener letras y espacios.");
        }

        if (!nameRegex.test(driver)) {
            isValid = false;
            messages.push("El nombre del conductor solo debe contener letras y espacios.");
        }

        if (!phoneRegex.test(phone)) {
            isValid = false;
            messages.push("El teléfono debe tener exactamente 8 dígitos numéricos.");
        }

        if (!plateRegex.test(plate)) {
            isValid = false;
            messages.push("La placa debe tener entre 5 y 10 caracteres alfanuméricos.");
        }

        if (cost === "" || isNaN(cost) || parseFloat(cost) <= 0) {
            isValid = false;
            messages.push("El costo debe ser un número mayor a 0.");
        }

        if (!isValid) {
            Swal.fire({
                icon: 'error',
                title: 'Error en los datos',
                html: messages.join("<br>"),
                confirmButtonText: 'Entendido',
                customClass: {
                    popup: 'swal2-agrow-popup',
                    title: 'swal2-agrow-title',
                    confirmButton: 'btn btn-success'
                },
                buttonsStyling: false
            });
        }

        return isValid;
    }

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



    const confirmableForms = document.querySelectorAll('form.confirm-action');

    confirmableForms.forEach(form => {
        form.addEventListener('submit', (event) => {
            event.preventDefault();

            const title = form.dataset.title || '¿Confirmar acción?';
            const message = form.dataset.message || '¿Estás seguro de realizar esta acción?';

            if (form.id === 'editTransportForm' && !validateQuantityInput()) {
                Swal.fire({
                    title: 'Error de Validación',
                    text: 'Por favor, corrija la cantidad transportada.',
                    icon: 'error',
                    confirmButtonText: 'Entendido',
                    customClass: {
                        popup: 'swal2-agrow-popup',
                        title: 'swal2-agrow-title',
                        confirmButton: 'btn btn-success'
                    },
                    buttonsStyling: false
                });
                return;
            }

            if (form.action.includes('/transport/register') && !validateTransportFormInputs()) {
                return;
            }

            Swal.fire({
                title: title,
                text: message,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Sí, confirmar',
                cancelButtonText: 'Cancelar',
                reverseButtons: true,
                customClass: {
                    popup: 'swal2-agrow-popup',
                    title: 'swal2-agrow-title',
                    confirmButton: 'btn btn-success', 
                    cancelButton: 'btn btn-danger'    
                },
                buttonsStyling: false
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
                        confirmButtonText: 'Aceptar',
                        customClass: {
                            popup: 'swal2-agrow-popup',
                            title: 'swal2-agrow-title',
                            confirmButton: 'btn btn-confirm'
                        },
                        buttonsStyling: false
                    }).then(() => {
                        form.submit();
                    });
                }
            });
        });
    });
});
