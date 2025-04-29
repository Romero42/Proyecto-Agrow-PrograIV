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
    if (window.flatpickr) {
      flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: "today" });
      flatpickr('.date-picker-dmy', { dateFormat: 'd/m/Y', allowInput: true, maxDate: "today" });
      flatpickr('.date-picker-future-dmy', { dateFormat: "d/m/Y", allowInput: true, minDate: "today" });
      flatpickr('.date-picker-future-iso', { dateFormat: "Y-m-d", allowInput: true, minDate: "today" });
    }
  }
  initFlatpickr();

  const formatColones = val => {
    const number = parseFloat(val);
    if (isNaN(number)) return '₡ 0.00';
    return new Intl.NumberFormat('es-CR', { style: 'currency', currency: 'CRC', minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(number);
  }

  function formatPrices() {
    document.querySelectorAll('.price-display').forEach(el => {
      const value = el.dataset.value || el.textContent;
      el.textContent = formatColones(value);
    });
  }
  formatPrices();

  function attachConfirmHandlers() {
    document.querySelectorAll('.confirm-action').forEach(el => {
      if (el.dataset.confirmHandlerAttached) return;
      el.dataset.confirmHandlerAttached = 'true';
      const message = el.dataset.message || '¿Estás seguro?';
      const title = el.dataset.title || 'Confirmar Acción';
      const confirmText = el.dataset.confirmText || 'Sí, continuar';
      const cancelText = el.dataset.cancelText || 'Cancelar';
      const icon = el.dataset.icon || 'warning';

      if (el.tagName === 'FORM') {
        el.addEventListener('submit', function(event) {
          event.preventDefault();
          const form = this;
          swalAgrow.fire({ title, text: message, icon, showCancelButton: true, confirmButtonText: confirmText, cancelButtonText: cancelText, reverseButtons: true })
            .then((result) => { if (result.isConfirmed) { form.submit(); } });
        });
      } else if (el.tagName === 'A') {
        el.addEventListener('click', function(event) {
          event.preventDefault();
          const link = this;
          const href = link.getAttribute('href');
          swalAgrow.fire({ title, text: message, icon, showCancelButton: true, confirmButtonText: confirmText, cancelButtonText: cancelText, reverseButtons: true })
            .then((result) => { if (result.isConfirmed) { window.location.href = href; } });
        });
      }
    });
  }
  attachConfirmHandlers();

  const filterFormSupply = document.getElementById('filter-form-supply');
  const supplyListContent = document.getElementById('supply-list-content');

  if (filterFormSupply && supplyListContent) {
    filterFormSupply.addEventListener('submit', e => {
      e.preventDefault();
      supplyListContent.style.opacity = '0.5';
      const formData = new FormData(filterFormSupply);
      const params = new URLSearchParams(formData);
      const url = filterFormSupply.action + '?' + params.toString();

      fetch(url, { method: 'GET', headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          return response.text();
        })
        .then(html => {
          const parser = new DOMParser();
          const doc = parser.parseFromString(html, 'text/html');
          const newContent = doc.getElementById('supply-list-content');
          if (newContent) {
            supplyListContent.innerHTML = newContent.innerHTML;
          } else {
            console.error('Contenedor #supply-list-content no encontrado en respuesta AJAX.');
            supplyListContent.innerHTML = '<p class="info-card">Error al actualizar la lista.</p>';
          }
          formatPrices();
          attachConfirmHandlers();
          initFlatpickr();
        })
        .catch(error => {
          console.error('Error en fetch:', error);
          supplyListContent.innerHTML = '<p class="info-card">Error al cargar filtros. Intente de nuevo.</p>';
          swalAgrow.fire('Error', 'No se pudo actualizar la lista.', 'error');
        })
        .finally(() => {
          supplyListContent.style.opacity = '1';
        });
    });
  }

  const swalMsgElement = document.getElementById('swal-message');
  if (swalMsgElement) {
    const message = swalMsgElement.dataset.mensaje;
    const error = swalMsgElement.dataset.error;
    if (message) {
      swalAgrow.fire({ title: '¡Éxito!', text: message, icon: 'success', timer: 2500, timerProgressBar: true });
    }
    if (error) {
      swalAgrow.fire({ title: 'Error', text: error, icon: 'error' });
    }
  }
});
