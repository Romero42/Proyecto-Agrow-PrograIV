document.addEventListener('DOMContentLoaded', () => {
    formatPrices();
    mostrarMensajeDesdeServidor();

    const confirmableForms = document.querySelectorAll('form.confirm-action');

    confirmableForms.forEach(form => {
        form.addEventListener('submit', (event) => {
            event.preventDefault();

            const title = form.dataset.title || '¿Confirmar acción?';
            const message = form.dataset.message || '¿Estás seguro de realizar esta acción?';

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
                    // Enviar el formulario normalmente
                    form.submit();
                }
            });
        });
    });
});

function formatPrices() {
    const priceElements = document.querySelectorAll('.price-display');
    priceElements.forEach(elem => {
        const value = parseFloat(elem.getAttribute('data-value'));
        if (!isNaN(value)) {
            elem.textContent = new Intl.NumberFormat('es-CR', {
                style: 'currency',
                currency: 'CRC',
                minimumFractionDigits: 0
            }).format(value);
        } else {
            elem.textContent = 'N/A';
        }
    });
}

function mostrarMensajeDesdeServidor() {
    const mensajeElem = document.getElementById('swal-message');
    if (!mensajeElem) return;

    const mensaje = mensajeElem.getAttribute('data-mensaje');
    const error = mensajeElem.getAttribute('data-error');

    if (mensaje) {
        Swal.fire({
            icon: 'success',
            title: 'ÉXITO',
            text: mensaje,
            confirmButtonText: 'Aceptar'
        });
    }

    if (error) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: error,
            confirmButtonText: 'Aceptar'
        });
    }
}