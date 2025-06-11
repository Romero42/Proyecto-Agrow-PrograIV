// resources/static/js/requests.js

document.addEventListener('DOMContentLoaded', () => {

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

       function initFlatpickr() {
           if (!window.flatpickr) {
               console.warn("La librería Flatpickr no está cargada.");
               return;
           }

           flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
           flatpickr('.date-picker-future-iso', { dateFormat: 'Y-m-d', allowInput: true, minDate: 'today' });
           flatpickr('.date-picker-dmy', { dateFormat: 'd/m/Y', allowInput: true });

           const start = document.querySelector("#rentStartDay");
           const end = document.querySelector("#rentFinalDay");
           if (start && end) {
               try {
                   const startPicker = flatpickr(start, { dateFormat: "Y-m-d", allowInput: true });
                   const endPicker = flatpickr(end, { dateFormat: "Y-m-d", allowInput: true });

                   startPicker.config.onChange.push(function (selectedDates) {
                       if (selectedDates.length) {
                           endPicker.set("minDate", selectedDates[0]);
                       } else {
                           endPicker.set("minDate", null);
                       }
                       if (endPicker.selectedDates.length && selectedDates.length && endPicker.selectedDates[0] < selectedDates[0]) {
                           endPicker.clear();
                       }
                   });

                   if (startPicker.selectedDates.length) {
                       endPicker.set("minDate", startPicker.selectedDates[0]);
                       if (endPicker.selectedDates.length && endPicker.selectedDates[0] < startPicker.selectedDates[0]) {
                           endPicker.clear();
                       }
                   }

               } catch (e) {
                   console.error("Error al inicializar pickers de rango:", e);
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

       function attachConfirmHandlers() {
           document.querySelectorAll('.confirm-action').forEach(el => {
               if (el.dataset.confirmAttached) return;
               el.dataset.confirmAttached = 'true';

               const {
                   message = '¿Está seguro de realizar esta acción?',
                   title = 'Confirmar Acción',
                   confirmText = 'Sí, proceder',
                   cancelText = 'Cancelar',
                   icon = 'warning'
               } = el.dataset;

               const showConfirmationDialog = (callback) => {
                   let formIsValid = true;
                   if (el.tagName === 'FORM') {
                       if (typeof el.checkValidity === 'function' && !el.checkValidity()) {
                           el.reportValidity();
                           formIsValid = false;
                       }
                       if ((el.id === 'saleForm' || el.id === 'editSaleForm') && !validateQuantityInput()) {
                           formIsValid = false;
                       }
                   }
                   if (!formIsValid) {
                       console.warn("Formulario no válido, confirmación cancelada.");
                       return;
                   }
                   swalAgrow.fire({
                       title: title,
                       text: message,
                       icon: icon,
                       showCancelButton: true,
                       confirmButtonText: confirmText,
                       cancelButtonText: cancelText,
                       reverseButtons: true
                   }).then((result) => {
                       if (result.isConfirmed) callback();
                   });
               };

               if (el.tagName === 'FORM') {
                   el.addEventListener('submit', (event) => {
                       event.preventDefault();
                       showConfirmationDialog(() => { el.submit(); });
                   });
               } else if (el.tagName === 'A') {
                   el.addEventListener('click', (event) => {
                       event.preventDefault();
                       const linkUrl = el.href;
                       const target = el.target;
                       const {
                           message: linkMessage = '¿Está seguro de continuar?',
                           title: linkTitle = 'Confirmar Navegación',
                           confirmText: linkConfirm = 'Sí',
                           cancelText: linkCancel = 'No',
                           icon: linkIcon = 'question'
                       } = el.dataset;

                       if (linkUrl) {
                           swalAgrow.fire({
                               title: linkTitle,
                               text: linkMessage,
                               icon: linkIcon,
                               showCancelButton: true,
                               confirmButtonText: linkConfirm,
                               cancelButtonText: linkCancel,
                               reverseButtons: true
                           }).then((result) => {
                               if (result.isConfirmed) {
                                   if (target === '_blank') window.open(linkUrl, '_blank');
                                   else window.location.href = linkUrl;
                               }
                           });
                       }
                   });
               }
           });
       }
       attachConfirmHandlers();

       function handleAjaxRequest(url, contentDivId) {
           const contentDiv = document.getElementById(contentDivId);
           if (!contentDiv) {
               console.error(`Contenedor '${contentDivId}' no encontrado.`);
               swalAgrow.fire('Error', `Error interno: Contenedor '${contentDivId}' no encontrado.`, 'error');
               return;
           }

           const loader = document.createElement('div');
           loader.innerHTML = '<div style="text-align:center; padding: 20px;"><span class="material-symbols-outlined" style="font-size: 40px; animation: spin 1.5s linear infinite;">autorenew</span><p>Cargando...</p></div>';
           contentDiv.innerHTML = '';
           contentDiv.appendChild(loader);
           contentDiv.style.opacity = '0.7';

           fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
               .then(response => {
                   if (!response.ok) {
                       return response.text().then(text => { throw new Error(`HTTP error ${response.status}: ${text || response.statusText}`); });
                   }
                   return response.text();
               })
               .then(html => {
                   contentDiv.innerHTML = html;
                   contentDiv.style.opacity = '1';
                   formatPrices();
                   attachConfirmHandlers();
                   initFlatpickr();
                   attachDynamicEventListeners();
                   if (document.getElementById('editSaleForm') || document.getElementById('saleForm')) {
                        validateQuantityInput();
                        calculateAndDisplayTotalSale();
                   }
               })
               .catch(error => {
                   console.error(`Error al cargar contenido para ${contentDivId}:`, error);
                   let errorMsg = 'No se pudo cargar el contenido. Verifique la conexión o inténtelo más tarde.';
                   if (error.message.includes('HTTP error')) {
                       errorMsg = `Error al cargar: ${error.message}. Por favor, intente de nuevo.`;
                   }
                   swalAgrow.fire('Error de Carga', errorMsg, 'error');
                   contentDiv.innerHTML = `<div class="info-card"><span class="material-symbols-outlined icon">error</span><h2>Error de Carga</h2><p>No se pudieron cargar los datos (${error.message}). Por favor, actualice o intente de nuevo.</p></div>`;
                   contentDiv.style.opacity = '1';
               });
       }

   window.pageSuppliers = function(link) {
       if (!link) return;
       const page = link.getAttribute('data-page');

       const form = document.getElementById('filter-form-supplier');
       const formData = form ? new FormData(form) : new FormData();

       formData.set('page', page);

       const urlParams = new URLSearchParams(window.location.search);
       const searchParam = urlParams.get('search');
       if (searchParam && !formData.has('search')) {
           formData.set('search', searchParam);
       }

       console.log("Cargando página de proveedores:", page);
       console.log("URL:", `/suppliers/table?${new URLSearchParams(formData).toString()}`);

       handleAjaxRequest(
          `/suppliers/table?${new URLSearchParams(formData).toString()}`,
          'supplier-list-content'
       );
       return false;
   };window.pageRequests = function(link) {
         if (!link) return;
         const page = link.getAttribute('data-page');

         const form = document.getElementById('filter-form-request');
         const formData = form ? new FormData(form) : new FormData();

         formData.set('page', page);

         const urlParams = new URLSearchParams(window.location.search);
         const searchParam = urlParams.get('search');
         if (searchParam && !formData.has('search')) {
             formData.set('search', searchParam);
         }

         handleAjaxRequest(
              `/requests/table?${new URLSearchParams(formData).toString()}`,
             'request-list-content'
         );
   };

       function attachDynamicEventListeners() {
           document.body.addEventListener('submit', function(event) {
               const form = event.target.closest('form.filter-section');
               if (!form) return;


               event.preventDefault();

               let contentId, actionUrl;

               if (form.id === 'filter-form-request') {
                                  contentId = 'request-list-content';
                                  actionUrl = '/requests/table';

               } else {
                   console.warn('Formulario de filtro con ID no reconocido:', form.id);
                   return;
               }

               const formData = new FormData(form);
               formData.set('page', '0');
               handleAjaxRequest(`${actionUrl}?${new URLSearchParams(formData).toString()}`, contentId);
           });

           document.body.addEventListener('click', function(event) {
               const pgLink = event.target.closest('.pagination a[data-page]');
               if (pgLink) {
                   event.preventDefault();
                   const container = pgLink.closest('[id$="-list-content"]');
                   if (container) {
                       if (container.id === 'request-list-content') window.pageRequests(pgLink);{
                        window.pageSuppliers(pgLink);
                       }
                   }
               }
           });
       }
       attachDynamicEventListeners();

       const flash = document.getElementById('swal-message');
       if (flash) {
           if (flash.dataset.mensaje) {
               swalAgrow.fire({
                   title: '¡Éxito!',
                   text: flash.dataset.mensaje,
                   icon: 'success'
               });
           }
           if (flash.dataset.error) {
               swalAgrow.fire({
                   title: 'Error',
                   text: flash.dataset.error,
                   icon: 'error'
               });
           }
       }

   });