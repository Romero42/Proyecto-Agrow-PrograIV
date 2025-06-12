document.addEventListener('DOMContentLoaded', () => {

    // === MANEJO DE MENSAJES SWEETALERT ===
    const swalMessage = document.getElementById('swal-message');
    if (swalMessage) {
        const mensaje = swalMessage.getAttribute('data-mensaje');
        const error = swalMessage.getAttribute('data-error');
        
        if (mensaje) {
            Swal.fire({
                icon: 'success',
                title: '¡Éxito!',
                text: mensaje,
                confirmButtonColor: '#28a745'
            });
        }
        
        if (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error,
                confirmButtonColor: '#dc3545'
            });
        }
    }

    // === MANEJO DE CONFIRMACIONES PARA ELEMENTOS CON CLASE CONFIRM-ACTION ===
    const confirmElements = document.querySelectorAll('.confirm-action');
    
    confirmElements.forEach(element => {
        element.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const title = this.getAttribute('data-title') || '¿Confirmar acción?';
            const message = this.getAttribute('data-message') || '¿Estás seguro de que deseas continuar?';
            
            Swal.fire({
                title: title,
                text: message,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#007bff',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Sí, confirmar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.submit(); // Solo se envía el formulario, sin mostrar mensaje de éxito
                }
            });
        });
    });

    // === FUNCIONES DE CÁLCULO Y VALIDACIÓN ===
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
                    form.submit(); // No muestra un segundo mensaje, solo envía
                }
            });
        });
    });

    // === FUNCIONALIDAD DE PAGINACIÓN ===

    function handleAjaxRequest(url, contentDivId) {
        const contentDiv = document.getElementById(contentDivId);
        if (!contentDiv) {
            console.error(`Contenedor '${contentDivId}' no encontrado.`);
            return;
        }

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
            if (!response.ok) {
                throw new Error(`HTTP error ${response.status}`);
            }
            return response.text();
        })
        .then(html => {
            contentDiv.innerHTML = html;
            contentDiv.style.opacity = '1';
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

    function updateTransportCounter() {
        const totalItemsElement = document.querySelector('.page-subtitle span');
        if (totalItemsElement) {
            console.log('Contador actualizado desde el servidor');
        }
    }

    function pageTransport(link) {
        if (!link) return;

        const page = link.getAttribute('data-page');
        const transportType = document.getElementById('transport_type')?.value || '';
        const delivered = document.getElementById('delivered')?.value || '';

        let url = `/transport/table?page=${page}`;
        if (transportType) url += `&transport_type=${encodeURIComponent(transportType)}`;
        if (delivered) url += `&delivered=${delivered}`;

        handleAjaxRequest(url, 'tableData');
    }

    function filterTransport() {
        const transportType = document.getElementById('transport_type')?.value || '';
        const delivered = document.getElementById('delivered')?.value || '';

        let url = '/transport/table?page=0';
        if (transportType) url += `&transport_type=${encodeURIComponent(transportType)}`;
        if (delivered) url += `&delivered=${delivered}`;

        handleAjaxRequest(url, 'tableData');
    }

    document.addEventListener('click', function(event) {
        const link = event.target.closest('[data-page]');
        if (link) {
            event.preventDefault();
            pageTransport(link);
        }
    });

    document.getElementById('transport_type')?.addEventListener('change', filterTransport);
    document.getElementById('delivered')?.addEventListener('change', filterTransport);

});
