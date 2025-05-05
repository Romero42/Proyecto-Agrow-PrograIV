document.addEventListener('DOMContentLoaded', () => {
    // Configurar SweetAlert2 con estilos de Agrow
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

    // Función para inicializar Flatpickr
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
                    }
                });

                 if (startPicker.selectedDates.length && endPicker.selectedDates.length) {
                    if (endPicker.selectedDates[0] < startPicker.selectedDates[0]) {
                         endPicker.clear();
                    }
                     endPicker.set("minDate", startPicker.selectedDates[0]);
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

    const quantitySaleInput = document.getElementById('quantitySold');
    const priceSaleInput = document.getElementById('pricePerUnitSold');
    if (quantitySaleInput) {
        quantitySaleInput.addEventListener('input', calculateAndDisplayTotalSale);
    }
    if (priceSaleInput) {
        priceSaleInput.addEventListener('input', calculateAndDisplayTotalSale);
    }



    // Agrega confirmaciones a formularios y enlaces con clase 'confirm-action'
    function attachConfirmHandlers() {
        document.querySelectorAll('.confirm-action').forEach(el => {
            if (el.dataset.confirmAttached) return;
            el.dataset.confirmAttached = 'true';

            const {
                message = '¿Está seguro de realizar esta acción?',
                title = 'Confirmar Acción',
                confirmText = 'Sí, proceder',
                cancelText = 'Cancelar',
                icon = 'warning' // 'warning', 'question', 'info'
            } = el.dataset;

            const showConfirmationDialog = (callback) => {
                swalAgrow.fire({
                    title: title,
                    text: message,
                    icon: icon,
                    showCancelButton: true,
                    confirmButtonText: confirmText,
                    cancelButtonText: cancelText,
                    reverseButtons: true // Poner botón de confirmación a la derecha
                })
                    .then((result) => {
                        if (result.isConfirmed) {
                            callback(); // Ejecutar la acción si se confirma
                        }
                    });
            };

            // Asignar al evento correcto según el tipo de elemento
            if (el.tagName === 'FORM') {
                el.addEventListener('submit', (event) => {
                    event.preventDefault(); // Detener el envío normal
                    showConfirmationDialog(() => {
                        el.submit(); // Enviar el formulario si se confirma
                    });
                });
            } else if (el.tagName === 'A') {
                el.addEventListener('click', (event) => {
                    event.preventDefault(); // Detener la navegación normal
                    const linkUrl = el.href;
                    if (linkUrl) {
                        showConfirmationDialog(() => {
                            window.location.href = linkUrl; // Navegar si se confirma
                        });
                    }
                });
            }
            // Añadir aquí para botones si es necesario
            else if (el.tagName === 'BUTTON' && el.type !== 'submit') {
                 el.addEventListener('click', (event) => {
                      // Si es un botón que no es submit, tal vez navega o llama a JS
                      // Se necesita lógica específica aquí basada en qué hace el botón
                      // showConfirmationDialog(() => { /* acción del botón */ });
                 });
            }
        });
    }

    attachConfirmHandlers(); // Asigna confirmaciones al cargar

    // ----- Función general para manejar peticiones AJAX de paginación/filtrado -----
    function handleAjaxRequest(url, contentDivId) {
        const contentDiv = document.getElementById(contentDivId);
        if (!contentDiv) {
            console.error(`Element with ID '${contentDivId}' not found.`);
            swalAgrow.fire('Error', `Error interno: Contenedor '${contentDivId}' no encontrado.`, 'error');
            return;
        }
        contentDiv.style.opacity = '0.5'; // Indicate loading

        fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.text();
            })
            .then(html => {
                contentDiv.innerHTML = html;
                // Re-initialize functionalities for the new content
                formatPrices();
                attachConfirmHandlers();
                initFlatpickr();
                attachDynamicEventListeners(); // Re-attach listeners for new elements
                // Specific re-init if needed, e.g., calculateAndDisplayTotalSale(); if applicable
            })
            .catch(error => {
                console.error(`Error fetching content for ${contentDivId}:`, error);
                swalAgrow.fire('Error', 'No se pudo cargar el contenido solicitado. Verifique la conexión o inténtelo más tarde.', 'error');
                contentDiv.innerHTML = `<div class="info-card"><span class="material-symbols-outlined icon">error</span><h2>Error de Carga</h2><p>No se pudieron cargar los datos. Por favor, actualice la página o inténtelo de nuevo.</p></div>`;
            })
            .finally(() => {
                contentDiv.style.opacity = '1'; // Restore opacity
            });
    }

    // ----- Delegación de eventos para elementos cargados dinámicamente (paginación, etc.) -----
    function attachDynamicEventListeners() {
        // Listener para paginación de Cosechas
        document.getElementById('harvest-list-content')?.addEventListener('click', function(event) {
             const target = event.target.closest('a[data-page]'); // Busca el enlace de paginación
             if (target && target.closest('.pagination')) { // Asegura que esté dentro de la paginación
                  event.preventDefault();
                  const page = target.getAttribute('data-page');
                  const stateC = target.getAttribute('data-state'); // Pasar filtros actuales
                  const destinyC = target.getAttribute('data-destiny');

                  const params = new URLSearchParams();
                  params.append('page', page);
                  if (stateC && stateC !== 'null' && stateC !== '') params.append('stateC', stateC);
                  if (destinyC && destinyC !== 'null' && destinyC !== '') params.append('destinyC', destinyC);

                  handleAjaxRequest(`/harvests/page?${params.toString()}`, 'harvest-list-content');
             }
        });

        // Listener para paginación de Suministros
        document.getElementById('supply-list-content')?.addEventListener('click', function(event) {
            const target = event.target.closest('a[data-page]');
            if (target && target.closest('.pagination')) {
                 event.preventDefault();
                 const page = target.getAttribute('data-page');
                 const filterForm = document.getElementById('filter-form-supply');
                 const formData = filterForm ? new FormData(filterForm) : new FormData();
                 formData.set('page', page); // Actualiza la página en los datos del form

                 handleAjaxRequest(`/supplies/table?${new URLSearchParams(formData).toString()}`, 'supply-list-content');
            }
        });

        // Listener para paginación/filtrado de Productores
        document.getElementById('table-producer')?.addEventListener('click', function(event) {
            const target = event.target.closest('a[data-page]'); // Link de paginación
             if (target && target.closest('.pagination')) {
                  event.preventDefault();
                  const currentPage = target.getAttribute('data-page');
                  const filterForm = document.getElementById('filter-form-producer'); // El form tiene los filtros
                  const formData = filterForm ? new FormData(filterForm) : new FormData();
                  formData.set('page', currentPage); // Establece la página

                  // Asegurar que se envíen los filtros correctos (ciudad o id) si existen en el form
                  const city = formData.get('city');
                  const idProducer = formData.get('id_producer');

                  // Limpiar parámetros que no se usan para evitar conflictos
                  if(city && idProducer) {
                       // Si ambos están (aunque la UI no debería permitirlo), priorizamos? O limpiamos uno?
                       // Por ahora, dejemos que el backend maneje la prioridad o error.
                  }

                   // Construye URL basado en formData
                  handleAjaxRequest(`/producers/list?${new URLSearchParams(formData).toString()}`, 'table-producer');
             }
        });

         // Listener para paginación/filtrado de Alquileres
         document.getElementById('tableData')?.addEventListener('click', function(event) {
              const target = event.target.closest('a[data-page]'); // Links de paginación
               if (target && target.closest('.pagination')) {
                    event.preventDefault();
                    const page = target.getAttribute('data-page');
                    const filterForm = document.getElementById('filter-form-rent'); // Formulario de filtros

                    const rentStartDay = filterForm?.querySelector('#rentStartDay')?.value;
                    const rentFinalDay = filterForm?.querySelector('#rentFinalDay')?.value;
                    const idMaquina = filterForm?.querySelector('#id_maquina')?.value;

                     const params = new URLSearchParams();
                     params.append('page', page);

                     // Lógica de filtrado igual que en la función filterRent original
                     if (rentStartDay && !rentFinalDay && !idMaquina) {
                          params.append('rentStartDay', rentStartDay);
                     } else if (!rentStartDay && rentFinalDay && !idMaquina) {
                          params.append('rentFinalDay', rentFinalDay);
                     } else if (rentStartDay && rentFinalDay && !idMaquina) {
                          params.append('rentStartDay', rentStartDay);
                          params.append('rentFinalDay', rentFinalDay);
                     } else if (!rentStartDay && !rentFinalDay && idMaquina) {
                          params.append('id_maquina', idMaquina);
                     } else if (!rentStartDay && !rentFinalDay && !idMaquina){
                         // Caso sin filtro, solo paginación normal
                     } else {
                          // Caso inválido (múltiples filtros activos), no hacer nada o mostrar error
                           console.warn("Multiple rent filters active, request aborted.");
                           // mostrarAlerta("error", "Use solo un tipo de filtro a la vez."); // Requeriría mostrarAlerta aquí
                           return;
                     }

                     const endpoint = params.has('rentStartDay') || params.has('rentFinalDay') || params.has('id_maquina')
                                        ? '/rent/tableFilter' // Endpoint de filtro
                                        : '/rent/pageCurrent'; // Endpoint de paginación normal

                     handleAjaxRequest(`${endpoint}?${params.toString()}`, 'tableData');
               }

               // Listener para el botón "Ver Maquina"
               const viewMachineButton = event.target.closest('a[data-id]');
                if (viewMachineButton && !viewMachineButton.closest('.pagination')) { // Asegura que no sea un link de paginación
                     event.preventDefault();
                     const machineId = viewMachineButton.getAttribute('data-id');
                     const infoDiv = document.getElementById('infoMaquina');
                     if (infoDiv) {
                          handleAjaxRequest(`/rent/viewMaquina?id_maquina=${machineId}`, 'infoMaquina');
                     }
                }
         });

         // Listener para el botón "Cerrar Info Maquina" (si existe dinámicamente)
         document.getElementById('infoMaquina')?.addEventListener('click', function(event) {
              if (event.target.closest('a') && event.target.closest('a').textContent.includes('close')) { // Simple check for close icon/link
                   event.preventDefault();
                   this.innerHTML = ""; // Clear the content
              }
         });

          // Listener para botones de submit en formularios de filtro (que ahora usarán AJAX)
          // Filtro Productores
           document.getElementById('filter-form-producer')?.addEventListener('submit', function(event) {
                event.preventDefault();
                const formData = new FormData(this);
                formData.set('page', '0'); // Filtrar siempre empieza en página 0
                handleAjaxRequest(`/producers/list?${new URLSearchParams(formData).toString()}`, 'table-producer');
           });

           // Filtro Alquileres
           document.getElementById('filter-form-rent')?.addEventListener('submit', function(event) {
                 event.preventDefault();
                 // La lógica de validación y construcción de URL ya está en el listener de paginación.
                 // Podemos reutilizarla o simplificarla aquí.
                 const rentStartDay = this.querySelector('#rentStartDay').value;
                 const rentFinalDay = this.querySelector('#rentFinalDay').value;
                 const idMaquina = this.querySelector('#id_maquina').value;
                 const params = new URLSearchParams();
                 params.append('page', '0'); // Filtrar empieza en página 0

                 if (rentStartDay && rentFinalDay && idMaquina) {
                     swalAgrow.fire("Error", "Use solo un filtro a la vez.", "error"); return;
                 } else if (rentStartDay && idMaquina) {
                      swalAgrow.fire("Error", "Use solo un filtro a la vez.", "error"); return;
                 } else if (rentFinalDay && idMaquina) {
                       swalAgrow.fire("Error", "Use solo un filtro a la vez.", "error"); return;
                 } else if (!rentStartDay && !rentFinalDay && !idMaquina) {
                     swalAgrow.fire("Info", "Por favor ingrese un filtro.", "info"); return;
                 }

                  if (rentStartDay) params.append('rentStartDay', rentStartDay);
                  if (rentFinalDay) params.append('rentFinalDay', rentFinalDay);
                  if (idMaquina) params.append('id_maquina', idMaquina);

                  handleAjaxRequest(`/rent/tableFilter?${params.toString()}`, 'tableData');
           });

            // Filtro Suministros (ya estaba usando AJAX, asegurarse que siga funcionando)
            document.getElementById('filter-form-supply')?.addEventListener('submit', function(event) {
                 event.preventDefault();
                 const formData = new FormData(this);
                 formData.set('page', '0'); // Reset page on filter submit
                 handleAjaxRequest(`/supplies/table?${new URLSearchParams(formData).toString()}`, 'supply-list-content');
            });

             // Filtro Cosechas por Calidad
            document.getElementById('filter-form-state')?.addEventListener('submit', function(event) {
                event.preventDefault();
                const formData = new FormData(this);
                formData.set('page', '0');
                handleAjaxRequest(`/harvests/page?${new URLSearchParams(formData).toString()}`, 'harvest-list-content');
            });

            // Filtro Cosechas por Destino
            document.getElementById('filter-form-destiny')?.addEventListener('submit', function(event) {
                event.preventDefault();
                const formData = new FormData(this);
                formData.set('page', '0');
                handleAjaxRequest(`/harvests/page?${new URLSearchParams(formData).toString()}`, 'harvest-list-content');
            });

    }

    attachDynamicEventListeners();

    // Muestra mensajes flash del servidor (éxito/error)
    const flash = document.getElementById('swal-message');
    if (flash) {
        if (flash.dataset.mensaje) swalAgrow.fire({
            title: '¡Éxito!',
            text: flash.dataset.mensaje,
            icon: 'success',
            timer: 2500,
            timerProgressBar: true
        });
        if (flash.dataset.error) swalAgrow.fire({
             title: 'Error',
             text: flash.dataset.error,
             icon: 'error'

        });
    }

    function updateMaxQuantityAndPrice() {
        const harvestSelect = document.getElementById('harvestId');
        const quantityInput = document.getElementById('quantitySold');
        const priceInput = document.getElementById('pricePerUnitSold');
        const quantityError = document.getElementById('quantityError');

        if (!harvestSelect || !quantityInput || !quantityError) return;

        const selectedOption = harvestSelect.options[harvestSelect.selectedIndex];

        if (selectedOption && selectedOption.value) {
            const available = parseInt(selectedOption.getAttribute('data-available'), 10);
            const defaultPrice = parseFloat(selectedOption.getAttribute('data-price') || '0');

            if (!isNaN(available) && available > 0) {
                quantityInput.max = available;
                quantityInput.placeholder = `Máximo disponible: ${available} kg`;
                quantityInput.disabled = false;

                // Ajustar valor si excede el máximo
                if (parseInt(quantityInput.value, 10) > available) {
                    quantityInput.value = available;
                }

                // Validar visualmente
                validateQuantityInput();

            } else {
                quantityInput.max = 0;
                quantityInput.placeholder = 'No disponible';
                quantityInput.value = '';
                quantityInput.disabled = true;
                quantityError.style.display = 'none';
            }
        } else {
            quantityInput.max = null;
            quantityInput.placeholder = 'Seleccione cosecha primero';
            quantityInput.value = '';
            quantityInput.disabled = true;
            quantityError.style.display = 'none';
        }
         calculateAndDisplayTotalSale(); // Recalcular total al cambiar cosecha
    }

    function validateQuantityInput() {
        const quantityInput = document.getElementById('quantitySold');
         const quantityError = document.getElementById('quantityError');
         if (!quantityInput || !quantityError) return;

          const max = parseInt(quantityInput.max, 10);
          const current = parseInt(quantityInput.value, 10);

          if (!isNaN(max) && !isNaN(current) && current > max) {
               quantityError.style.display = 'block';
               quantityInput.classList.add('input-error');
          } else {
               quantityError.style.display = 'none';
                quantityInput.classList.remove('input-error');
          }
           calculateAndDisplayTotalSale(); // Recalcular total al cambiar cantidad
    }

    const harvestSelect = document.getElementById('harvestId');
    if (harvestSelect) {
        harvestSelect.addEventListener('change', updateMaxQuantityAndPrice);
        // Llamar una vez al cargar por si hay preselección
        updateMaxQuantityAndPrice();
    }

    if (quantitySaleInput) {
        quantitySaleInput.addEventListener('input', validateQuantityInput);
    }

});