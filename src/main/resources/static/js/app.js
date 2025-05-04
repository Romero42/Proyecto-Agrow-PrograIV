
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
        
        return; 
        }
        flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
        flatpickr('.date-picker-future-iso', { dateFormat: 'Y-m-d', allowInput: true, minDate: 'today' });

        const start = document.querySelector("#rentStartDay");
        const end = document.querySelector("#rentFinalDay");

        if (start && end) {
        const startPicker = flatpickr(start, { dateFormat: "Y-m-d", allowInput: true, enableTime: false });
        const endPicker = flatpickr(end, { dateFormat: "Y-m-d", allowInput: true, enableTime: false });

        startPicker.config.onChange.push(function (selectedDates) {
        if (selectedDates.length) {
        
        endPicker.set("minDate", selectedDates[0]);
        
        if (endPicker.selectedDates.length && endPicker.selectedDates[0] < selectedDates[0]) {
        endPicker.setDate(selectedDates[0], false); 
        }
        } else {
        endPicker.set("minDate", null); 
        }
        });
        endPicker.config.onChange.push(function(selectedDates) {
        if (selectedDates.length && startPicker.selectedDates.length) {
        
        startPicker.set("maxDate", selectedDates[0]);
        } else if (!selectedDates.length) {
        startPicker.set("maxDate", null); 
        }
        });
        }
        }

        initFlatpickr();

        
        const formatColones = val => {
        const num = parseFloat(val);
        if (isNaN(num)) return '₡\u00A00.00'; 
        return new Intl.NumberFormat('es-CR', {
        style: 'currency',
        currency: 'CRC',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
        }).format(num);
        };

        
        function formatNumbers() {
        document.querySelectorAll('.price-display').forEach(el => {
        const value = el.dataset.value || el.textContent;
        el.textContent = formatColones(value);
        });
        document.querySelectorAll('.numeric-display').forEach(el => {
        const value = el.dataset.value || el.textContent;
        const num = parseFloat(value);
        if (!isNaN(num)) {
        
        el.textContent = new Intl.NumberFormat('es-CR', { minimumFractionDigits: 0, maximumFractionDigits: 2 }).format(num);
        }
        });
        }

        formatNumbers(); 

        
        function attachConfirmHandlers() {
        document.querySelectorAll('.confirm-action:not([data-confirm-attached="true"])').forEach(el => {
        el.dataset.confirmAttached = 'true'; 
        const {
        message = '¿Está seguro de realizar esta acción?',
        title = 'Confirmar',
        confirmText = 'Sí, continuar',
        cancelText = 'Cancelar',
        icon = 'warning' 
        } = el.dataset;

        const showDialog = (callback) => {
        swalAgrow.fire({
        title: title,
        text: message,
        icon: icon,
        showCancelButton: true,
        confirmButtonText: confirmText,
        cancelButtonText: cancelText,
        reverseButtons: true
        }).then((result) => {
        if (result.isConfirmed) {
        if (callback && typeof callback === 'function') {
        callback();
        }
        }
        });
        };

        if (el.tagName === 'FORM') {
        el.addEventListener('submit', (event) => {
        event.preventDefault(); 
        showDialog(() => {
        el.submit(); 
        });
        });
        } else if (el.tagName === 'A') {
        el.addEventListener('click', (event) => {
        event.preventDefault(); 
        showDialog(() => {
        window.location.href = el.href; 
        });
        });
        }
        });
        }

        attachConfirmHandlers(); 

        
        const supplyContentDiv = document.getElementById('supply-list-content');
        const supplyFilterForm = document.getElementById('filter-form-supply');

        
        function updateSupplyTable(url) {
        if (!supplyContentDiv) return;
        supplyContentDiv.style.opacity = '0.5'; 

        fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => {
        if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.text();
        })
        .then(html => {
        supplyContentDiv.innerHTML = html;
        
        formatNumbers();
        attachConfirmHandlers();
        initFlatpickr(); 
        })
        .catch(error => {
        console.error('AJAX Error:', error);
        supplyContentDiv.innerHTML = '<p class="info-card">Error al cargar los datos. Intente de nuevo.</p>';
        swalAgrow.fire('Error', 'No se pudo actualizar la tabla.', 'error');
        })
        .finally(() => {
        supplyContentDiv.style.opacity = '1'; 
        });
        }

        
        if (supplyFilterForm) {
        
        
        window.filterSupplies = () => {
        const formData = new FormData(supplyFilterForm);
        
        formData.set('page', '0');
        const params = new URLSearchParams(formData).toString();
        updateSupplyTable(`/supplies/table?${params}`);
        };
        }

        
        window.pageSupplies = (element) => {
        const page = element.getAttribute('data-page');
        const formData = new FormData(supplyFilterForm); 
        formData.set('page', page); 
        const params = new URLSearchParams(formData).toString();
        updateSupplyTable(`/supplies/table?${params}`);
        };

        
        const flash = document.getElementById('swal-message');
        if (flash) {
        if (flash.dataset.mensaje) {
        swalAgrow.fire({
        title: '¡Éxito!',
        text: flash.dataset.mensaje,
        icon: 'success',
        timer: 2500,
        timerProgressBar: true,
        toast: true, 
        position: 'top-end' 
        });
        }
        if (flash.dataset.error) {
        swalAgrow.fire({
        title: 'Error',
        text: flash.dataset.error,
        icon: 'error'
        });
        }
        
        flash.removeAttribute('data-mensaje');
        flash.removeAttribute('data-error');
        }

        
        
        const rentalTableUpdate = document.getElementById("tableData"); 
        const maquinaInfoDiv = document.getElementById("infoMaquina");

        window.mostrarAlerta = (tipo, mensaje) => { 
        swalAgrow.fire({ 
        icon: tipo,
        text: mensaje,
        title: tipo === 'error' ? '¡Error!' : (tipo === 'warning' ? 'Atención' : 'Información'),
        timer: 2500,
        timerProgressBar: true,
        confirmButtonText: 'OK'
        });
        }

        window.filterRent = (element) => { 
        if (!rentalTableUpdate) return;
        var rentStartDay = document.getElementById("rentStartDay")?.value; 
        var rentFinalDay = document.getElementById("rentFinalDay")?.value;
        var id_maquina = document.getElementById("id_maquina")?.value;
        var currentPage = element.getAttribute('data-page') || '0'; 

        var params = new URLSearchParams(); 
        var filterCount = 0;
        if (rentStartDay) { params.append("rentStartDay", rentStartDay); filterCount++; }
        if (rentFinalDay) { params.append("rentFinalDay", rentFinalDay); filterCount++; }
        if (id_maquina) { params.append("id_maquina", id_maquina); filterCount++; }

        if (filterCount === 0) {
        mostrarAlerta("error", "Debe seleccionar al menos un filtro (fechas o código de máquina).");
        return;
        }
        if (filterCount > 1 && id_maquina) {
        mostrarAlerta("error", "No puede combinar búsqueda por código de máquina con filtros de fecha.");
        return;
        }
        if (filterCount > 2) { 
        mostrarAlerta("error", "Use solo filtro de fechas o filtro de código de máquina.");
        return;
        }

        params.append("page", currentPage); 

        rentalTableUpdate.style.opacity = '0.5';
        fetch("/rent/tableFilter?" + params.toString(), { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => response.ok ? response.text() : Promise.reject('Network response was not ok.'))
        .then(html => { rentalTableUpdate.innerHTML = html; initFlatpickr(); attachConfirmHandlers(); }) 
        .catch(error => {
        console.error('Filter Rent Error:', error);
        rentalTableUpdate.innerHTML = '<p class="info-card">Error al cargar los datos.</p>';
        mostrarAlerta('error', 'No se pudo aplicar el filtro.');
        })
        .finally(() => { rentalTableUpdate.style.opacity = '1'; });
        }

        window.viewMaquina = (element) => { 
        if (!maquinaInfoDiv) return;
        var id_maquina = element.getAttribute('data-id');
        if (!id_maquina) return;

        maquinaInfoDiv.style.opacity = '0.5';
        fetch("/rent/viewMaquina?id_maquina=" + id_maquina, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => response.ok ? response.text() : Promise.reject('Network response was not ok.'))
        .then(html => { maquinaInfoDiv.innerHTML = html; }) 
        .catch(error => {
        console.error('View Maquina Error:', error);
        maquinaInfoDiv.innerHTML = '<p class="info-card">Error al cargar detalles.</p>';
        mostrarAlerta('error', 'No se pudieron cargar los detalles de la máquina.');
        })
        .finally(() => { maquinaInfoDiv.style.opacity = '1'; });
        }

        window.cerrarInfoMaquina = () => { 
        if (maquinaInfoDiv) maquinaInfoDiv.innerHTML = "";
        }

        window.pageRent = (element) => { 
        if (!rentalTableUpdate) return;
        var currentPage = element.getAttribute('data-page');
        if (currentPage === null) return;

        rentalTableUpdate.style.opacity = '0.5';
        fetch("/rent/pageCurrent?page=" + currentPage, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => response.ok ? response.text() : Promise.reject('Network response was not ok.'))
        .then(html => { rentalTableUpdate.innerHTML = html; initFlatpickr(); attachConfirmHandlers(); }) 
        .catch(error => {
        console.error('Page Rent Error:', error);
        rentalTableUpdate.innerHTML = '<p class="info-card">Error al cargar la página.</p>';
        mostrarAlerta('error', 'No se pudo cambiar de página.');
        })
        .finally(() => { rentalTableUpdate.style.opacity = '1'; });
        }

        
        const producerTableDiv = document.getElementById("table-producer");
        const producerFilterForm = document.getElementById("filter-form-producer"); 

        window.producer = (element) => { 
        if (!producerTableDiv || !producerFilterForm) return;

        const cityInput = document.getElementById("city");
        const idInput = document.getElementById("id_producer");
        const lastCityInput = document.getElementById("lastCity"); 
        const filterActiveInput = document.getElementById("filter"); 

        const city = cityInput ? cityInput.value : null;
        const id_producer = idInput ? idInput.value : null;
        const lastCity = lastCityInput ? lastCityInput.value : null; 
        const filterActive = filterActiveInput ? filterActiveInput.value === 'true' : false; 

        const currentPage = element.getAttribute('data-page') || '0';
        const buttonAction = element.getAttribute('data-button'); 

        const params = new URLSearchParams();
        let resetPage = false;

        
        if (buttonAction === '1') {
        if (city && id_producer) {
        mostrarAlerta("error", "Use solo un filtro a la vez (Provincia o Código).");
        return;
        }
        if (!city && !id_producer) {
        mostrarAlerta("error", "Seleccione una provincia o ingrese un código para filtrar.");
        return;
        }
        resetPage = true; 
        if (city) params.set("city", city);
        if (id_producer) params.set("id_producer", id_producer);

        } else { 
        
        if (filterActive) {
        
        if (lastCity) { 
        params.set("city", lastCity);
        } else if (id_producer) { 
        
        
        
        
        const currentSearchId = document.getElementById("id_producer")?.value; 
        if (currentSearchId) params.set("id_producer", currentSearchId);

        }
        }
        
        }

        params.set("page", resetPage ? '0' : currentPage); 

        producerTableDiv.style.opacity = '0.5';
        fetch("/producers/list?" + params.toString(), { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => response.ok ? response.text() : Promise.reject('Network response was not ok.'))
        .then(html => {
        producerTableDiv.innerHTML = html;
        attachConfirmHandlers(); 
        
        const newLastCityInput = producerTableDiv.querySelector("#lastCity");
        if (newLastCityInput && lastCityInput) {
        lastCityInput.value = newLastCityInput.value;
        }
        const newFilterActiveInput = producerTableDiv.querySelector("#filter");
        if (newFilterActiveInput && filterActiveInput) {
        filterActiveInput.value = newFilterActiveInput.value;
        }
        })
        .catch(error => {
        console.error('Producer AJAX Error:', error);
        producerTableDiv.innerHTML = '<p class="info-card">Error al cargar los datos.</p>';
        mostrarAlerta('error', 'No se pudo actualizar la tabla de productores.');
        })
        .finally(() => { producerTableDiv.style.opacity = '1'; });
        };


        }); 