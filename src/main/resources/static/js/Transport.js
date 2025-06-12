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

    // === INICIALIZACIÓN DE EVENT LISTENERS PARA FORMULARIOS ===
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

    // === FUNCIONALIDAD DE PAGINACIÓN ===

    // Función para manejar peticiones AJAX
    function handleAjaxRequest(url, contentDivId) {
        const contentDiv = document.getElementById(contentDivId);
        if (!contentDiv) {
            console.error(`Contenedor '${contentDivId}' no encontrado.`);
            return;
        }

        console.log('Cargando URL:', url);

        // Mostrar loader
        const loader = document.createElement('div');
        loader.innerHTML = '<div style="text-align:center; padding: 20px;"><span class="material-symbols-outlined" style="font-size: 40px; animation: spin 1.5s linear infinite;">autorenew</span><p>Cargando...</p></div>';
        contentDiv.innerHTML = '';
        contentDiv.appendChild(loader);
        contentDiv.style.opacity = '0.7';

        fetch(url, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'text/html'
            }
        })
        .then(response => {
            console.log('Response status:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP error ${response.status}`);
            }
            return response.text();
        })
        .then(html => {
            console.log('HTML recibido, longitud:', html.length);
            contentDiv.innerHTML = html;
            contentDiv.style.opacity = '1';
            
            // Actualizar el contador en el header
            updateTransportCounter();
        })
        .catch(error => {
            console.error('Error al cargar contenido:', error);
            contentDiv.innerHTML = `
                <div class="info-card">
                    <span class="material-symbols-outlined icon">error</span>
                    <h2>Error de Carga</h2>
                    <p>No se pudieron cargar los datos. Por favor, actualice la página.</p>
                </div>`;
            contentDiv.style.opacity = '1';
        });
    }

    // Función para actualizar el contador de transportes
    function updateTransportCounter() {
        // Buscar el elemento que muestra el total de elementos
        const totalItemsElement = document.querySelector('.page-subtitle span');
        
        if (totalItemsElement) {
            // El total viene del servidor, no necesitamos calcularlo aquí
            console.log('Contador actualizado desde el servidor');
        }
    }

    // Función para manejar la paginación
    function pageTransport(link) {
        if (!link) return;

        const page = link.getAttribute('data-page');
        console.log('Cambiando a página:', page);

        // Obtener filtros actuales
        const transportType = document.getElementById('transport_type')?.value || '';
        const delivered = document.getElementById('delivered')?.value || '';

        // Construir URL con parámetros
        let url = `/transport/table?page=${page}`;
        if (transportType) url += `&transport_type=${encodeURIComponent(transportType)}`;
        if (delivered) url += `&delivered=${delivered}`;

        console.log('URL solicitada:', url);
        handleAjaxRequest(url, 'tableData');
    }

    // Función para filtrar transportes
    function filterTransport() {
        const transportType = document.getElementById('transport_type')?.value || '';
        const delivered = document.getElementById('delivered')?.value || '';

        let url = '/transport/table?page=0'; // Siempre empezar en página 0 al filtrar
        if (transportType) url += `&transport_type=${encodeURIComponent(transportType)}`;
        if (delivered) url += `&delivered=${delivered}`;

        console.log('Filtrando con URL:', url);
        handleAjaxRequest(url, 'tableData');
    }

    // === EVENT LISTENERS PARA PAGINACIÓN Y FILTROS ===

    // Event listener para links de paginación (delegación de eventos)
    document.addEventListener('click', function(event) {
        // Buscar si el click fue en un link de paginación
        const paginationLink = event.target.closest('.pagination a[data-page]');
        if (paginationLink) {
            event.preventDefault();
            pageTransport(paginationLink);
            return;
        }
    });

    // Event listener para el botón de filtrar
    const filterButton = document.querySelector('.filter-btn');
    if (filterButton) {
        filterButton.addEventListener('click', function(event) {
            event.preventDefault();
            filterTransport();
        });
    }

    // Event listeners para cambios en los filtros (filtrado automático)
    const transportTypeSelect = document.getElementById('transport_type');
    const deliveredSelect = document.getElementById('delivered');

    if (transportTypeSelect) {
        transportTypeSelect.addEventListener('change', function() {
            console.log('Cambio en tipo de transporte:', this.value);
            // Opcional: filtrar automáticamente al cambiar
            // filterTransport();
        });
    }

    if (deliveredSelect) {
        deliveredSelect.addEventListener('change', function() {
            console.log('Cambio en estado de entrega:', this.value);
            // Opcional: filtrar automáticamente al cambiar
            // filterTransport();
        });
    }

    // Hacer las funciones disponibles globalmente por si se necesitan
    window.pageTransport = pageTransport;
    window.filterTransport = filterTransport;

    console.log('Sistema de paginación de transportes inicializado correctamente');
});

// Añadir estilos CSS para la animación del loader
const style = document.createElement('style');
style.textContent = `
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
`;
document.head.appendChild(style);