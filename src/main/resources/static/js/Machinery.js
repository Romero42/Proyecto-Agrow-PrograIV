document.addEventListener('DOMContentLoaded', function () {
    formatPrices();
    setupDeleteConfirmation();
});

/**
 * Formatea los valores de costo en la tabla usando formato de moneda.
 */
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

/**
 * Configura una alerta de confirmación antes de eliminar una máquina.
 */
function setupDeleteConfirmation() {
    const deleteForms = document.querySelectorAll('form[th\\:action*="/gestionar"]');

    deleteForms.forEach(form => {
        form.addEventListener('submit', function (e) {
            e.preventDefault();

            Swal.fire({
                title: '¿Eliminar esta máquina?',
                text: 'Esta acción no se puede deshacer.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    form.submit();
                }
            });
        });
    });
}
