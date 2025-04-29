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

  // Inicializa Flatpickr en los inputs de fecha, se usa flatPickr porque permite formato de fecha especifico
  function initFlatpickr() {
    if (!window.flatpickr) return console.warn('Flatpickr no encontrado');
    flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
    flatpickr('.date-picker-future-iso', { dateFormat: 'Y-m-d', allowInput: true, minDate: 'today' });
  }
  initFlatpickr();

  // Formatea valores como colones costarricenses
  const formatColones = val => {
    const num = parseFloat(val);
    if (isNaN(num)) return '₡ 0.00';
    return new Intl.NumberFormat('es-CR', {
      style: 'currency',
      currency: 'CRC',
      minimumFractionDigits: 2
    }).format(num);
  };

  // Aplica formato de moneda a elementos .price-display
  function formatPrices() {
    document.querySelectorAll('.price-display').forEach(el => {
      const value = el.dataset.value || el.textContent;
      el.textContent = formatColones(value);
    });
  }
  formatPrices();

  // Agrega confirmaciones a formularios y enlaces
  function attachConfirmHandlers() {
    document.querySelectorAll('.confirm-action').forEach(el => {
      if (el.dataset.confirmAttached) return;
      el.dataset.confirmAttached = 'true';
      const { message='¿Seguro?', title='Confirmar', confirmText='Sí', cancelText='Cancelar', icon='warning' } = el.dataset;
      const showDialog = (callback) => {
        swalAgrow.fire({ title, text: message, icon, showCancelButton: true, confirmButtonText: confirmText, cancelButtonText: cancelText, reverseButtons: true })
          .then(res => { if (res.isConfirmed) callback(); });
      };
      if (el.tagName === 'FORM') {
        el.addEventListener('submit', e => { e.preventDefault(); showDialog(() => el.submit()); });
      } else if (el.tagName === 'A') {
        el.addEventListener('click', e => { e.preventDefault(); showDialog(() => window.location = el.href); });
      }
    });
  }
  attachConfirmHandlers();

  // Lógica AJAX para filtrar suministros
  const filterForm = document.getElementById('filter-form-supply');
  const contentDiv = document.getElementById('supply-list-content');
  if (filterForm && contentDiv) {
    filterForm.addEventListener('submit', e => {
      e.preventDefault();
      contentDiv.style.opacity = '0.5';
      fetch(`${filterForm.action}?${new URLSearchParams(new FormData(filterForm))}`, {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
      })
      .then(r => { if (!r.ok) throw Error(r.status); return r.text(); })
      .then(html => {
        const doc = new DOMParser().parseFromString(html,'text/html');
        const newContent = doc.getElementById('supply-list-content');
        contentDiv.innerHTML = newContent ? newContent.innerHTML : '<p class="info-card">Error al actualizar.</p>';
        formatPrices();
        attachConfirmHandlers();
        initFlatpickr();
      })
      .catch(() => {
        contentDiv.innerHTML = '<p class="info-card">Error de conexión.</p>';
        swalAgrow.fire('Error','No se pudo filtrar.','error');
      })
      .finally(() => { contentDiv.style.opacity = '1'; });
    });
  }

  // Muestra mensajes flash del servidor
  const flash = document.getElementById('swal-message');
  if (flash) {
    if (flash.dataset.mensaje) swalAgrow.fire({ title:'¡Éxito!', text: flash.dataset.mensaje, icon:'success', timer:2500, timerProgressBar:true });
    if (flash.dataset.error) swalAgrow.fire({ title:'Error', text: flash.dataset.error, icon:'error' });

  }
});