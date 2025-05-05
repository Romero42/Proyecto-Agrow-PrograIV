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


        function initFlatpickr() {
        if (!window.flatpickr) {

        return;
        }
        flatpickr('.date-picker-iso', { dateFormat: 'Y-m-d', allowInput: true, maxDate: 'today' });
        flatpickr('.date-picker-future-iso', { dateFormat: 'Y-m-d', allowInput: true, minDate: 'today' });

        const start = document.querySelector("#rentStartDay");
        const end = document.querySelector("#rentFinalDay");

        if (start && end) {
            const startPicker = flatpickr(start, { dateFormat: "Y-m-d", allowInput: true });
            const endPicker = flatpickr(end, { dateFormat: "Y-m-d", allowInput: true });

            startPicker.config.onChange.push(function (selectedDates) {
                if (selectedDates.length) {
                    endPicker.set("minDate", selectedDates[0]);
                }
            });
        }
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
            const {
                message = '¿Seguro?',
                title = 'Confirmar',
                confirmText = 'Sí',
                cancelText = 'Cancelar',
                icon = 'warning'
            } = el.dataset;
            const showDialog = (callback) => {
                swalAgrow.fire({
                    title,
                    text: message,
                    icon,
                    showCancelButton: true,
                    confirmButtonText: confirmText,
                    cancelButtonText: cancelText,
                    reverseButtons: true
                })
                    .then(res => {
                        if (res.isConfirmed) callback();
                    });
            };
            if (el.tagName === 'FORM') {
                el.addEventListener('submit', e => {
                    e.preventDefault();
                    showDialog(() => el.submit());
                });
            } else if (el.tagName === 'A') {
                el.addEventListener('click', e => {
                    e.preventDefault();
                    showDialog(() => window.location = el.href);
                });
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
                headers: {'X-Requested-With': 'XMLHttpRequest'}
            })
                .then(r => {
                    if (!r.ok) throw Error(r.status);
                    return r.text();
                })
                .then(html => {
                    const doc = new DOMParser().parseFromString(html, 'text/html');
                    const newContent = doc.getElementById('supply-list-content');
                    contentDiv.innerHTML = newContent ? newContent.innerHTML : '<p class="info-card">Error al actualizar.</p>';
                    formatPrices();
                    attachConfirmHandlers();
                    initFlatpickr();
                })
                .catch(() => {
                    contentDiv.innerHTML = '<p class="info-card">Error de conexión.</p>';
                    swalAgrow.fire('Error', 'No se pudo filtrar.', 'error');
                })
                .finally(() => {
                    contentDiv.style.opacity = '1';
                });
        });
    }

    // Muestra mensajes flash del servidor
    const flash = document.getElementById('swal-message');
    if (flash) {
        if (flash.dataset.mensaje) swalAgrow.fire({
            title: '¡Éxito!',
            text: flash.dataset.mensaje,
            icon: 'success',
            timer: 2500,
            timerProgressBar: true
        });
        if (flash.dataset.error) swalAgrow.fire({title: 'Error', text: flash.dataset.error, icon: 'error'});

    }
});

//ventana de mensaje del tipo que se obtiene
function mostrarAlerta(tipo, mensaje) {
    Swal.fire({
        icon: tipo, // success, error, warning, info, question
        text: mensaje,
        title: '¡Error!',
        timer: 2500,
        timerProgressBar: true,
        confirmButtonText: 'OK'
    });
}

//logica para la tabla de alquileres
function filterRent(element) {

    var tableUpdate = document.getElementById("tableData");
    var rentStartDay = document.getElementById("rentStartDay").value;
    var rentFinalDay = document.getElementById("rentFinalDay").value;
    var id_maquina = document.getElementById("id_maquina").value;
    var currentPage = element.getAttribute('data-page');

    var params = "";
    var xhttp = new XMLHttpRequest();


    if (rentStartDay && rentFinalDay && id_maquina) {

        mostrarAlerta("error", "Use solo un filtro")
        return;
    } else if (!rentStartDay && !rentFinalDay && !id_maquina) {

        mostrarAlerta("error", "Filtros vacios")
        return;
    }else if(rentStartDay && id_maquina || rentFinalDay && id_maquina){

        mostrarAlerta("error", "Use solo un filtro")
        return;
    }

    if (rentStartDay && !rentFinalDay) {

        params = "rentStartDay=" + encodeURIComponent(rentStartDay)
        + "&page=" + encodeURIComponent(currentPage);
    } else if (!rentStartDay && rentFinalDay) {

        params = "rentFinalDay=" + encodeURIComponent(rentFinalDay)
        + "&page=" + encodeURIComponent(currentPage);
    } else if (rentStartDay && rentFinalDay) {

        params = "rentStartDay=" + encodeURIComponent(rentStartDay)
            + "&rentFinalDay=" + encodeURIComponent(rentFinalDay)
            + "&page=" + encodeURIComponent(currentPage);
    } else {

        params = "id_maquina=" + encodeURIComponent(id_maquina);
    }

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {

            tableUpdate.innerHTML = this.responseText;
        }
    };

    xhttp.open("GET", "/rent/tableFilter?" + params, true);
    xhttp.send();
}

//ver informacion de una maquina en alquiler
function viewMaquina(element){

    var maquinaInfo = document.getElementById("infoMaquina");
    var id_maquina = element.getAttribute('data-id');

    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {

            maquinaInfo.innerHTML = this.responseText;
        }
    };

    xhttp.open("GET", "/rent/viewMaquina?id_maquina=" + id_maquina, true);
    xhttp.send();
}

function cerrarInfoMaquina() {
    document.getElementById("infoMaquina").innerHTML = "";
}

//actualizar paginacion en listAlquiler
function pageRent(element){

    var tableCurrent = document.getElementById("tableData");
    var currentPage = element.getAttribute('data-page');

    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {

            tableCurrent.innerHTML = this.responseText;
        }
    };

    xhttp.open("GET", "/rent/pageCurrent?page=" + currentPage, true);
    xhttp.send();
}

function cerrarInfoMaquina() {
    document.getElementById("infoMaquina").innerHTML = "";
}

//actualizar paginacionProducer
function producer(element){

    var tableCurrent = document.getElementById("table-producer");
    var city = document.getElementById("city").value;
    var lastCity = document.getElementById("lastCity").value;
    var filter = document.getElementById("filter").value;
    var id_producer = document.getElementById("id_producer").value;
    var currentPage = element.getAttribute('data-page');
    var buttonAction = element.getAttribute('data-button');

    var params = "page=" + encodeURIComponent(currentPage);
    var xhttp = new XMLHttpRequest();

    if(city && id_producer && buttonAction == 1){
        mostrarAlerta("error", "Use solo un filtro")
        return;

    }else if(city === "" && buttonAction == 1 && !id_producer){

        mostrarAlerta("error", "Seleccione una ciudad o busque por id")
        return;
    }

     if(buttonAction == 1 && id_producer || buttonAction == 1 && city){
        currentPage = 0;
     }

     xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {

            tableCurrent.innerHTML = this.responseText;
        }
     };

      if(city && buttonAction == 1){

          params = "city=" + encodeURIComponent(city)
                 + "&page=" + encodeURIComponent(currentPage);
      }else if(id_producer && buttonAction == 1){

         params = "id_producer=" + encodeURIComponent(id_producer);
      }else if(buttonAction == 0 && city && filter === "true" ||
               buttonAction == 0 && id_producer && filter === "true"){

            if(lastCity){
                params = "page=" + encodeURIComponent(currentPage)
                     + "&city=" + encodeURIComponent(lastCity);
            }else{
                params = "page=" + encodeURIComponent(currentPage)
                        + "&city=" + encodeURIComponent(city);
            }
      }

    xhttp.open("GET", "/producers/list?"+params, true);
    xhttp.send();
}

// Funcion para paginacion AJAX de Cosechas
function pageHarvest(element) {
    const page = element.getAttribute('data-page');
    const stateC = element.getAttribute('data-state');
    const destinyC = element.getAttribute('data-destiny');

    const contentDiv = document.getElementById('harvest-list-content');
    if (!contentDiv) {
        console.error("Element with ID 'harvest-list-content' not found.");
        swalAgrow.fire('Error', 'Error interno: Contenedor de tabla no encontrado.', 'error');
        return;
    }
    contentDiv.style.opacity = '0.5';

    const params = new URLSearchParams();
    params.append('page', page);

    if (stateC && stateC !== 'null' && stateC !== '') {
        params.append('stateC', stateC);
    }
    if (destinyC && destinyC !== 'null' && destinyC !== '') {
        params.append('destinyC', destinyC);
    }

    const url = `/harvests/page?${params.toString()}`;

    fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => {
            if (!response.ok) {

                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.text();
        })
        .then(html => {

            contentDiv.innerHTML = html;

            attachConfirmHandlers();

        })
        .catch(error => {

            console.error('Error fetching harvest page:', error);
            swalAgrow.fire('Error', 'No se pudo cargar la página de cosechas solicitada. Verifique la conexión o inténtelo más tarde.', 'error');

            contentDiv.innerHTML = '<div class="info-card"><span class="material-symbols-outlined icon">error</span><h2>Error de Carga</h2><p>No se pudieron cargar los datos. Por favor, actualice la página o inténtelo de nuevo.</p></div>';
        })
        .finally(() => {

            contentDiv.style.opacity = '1';
        });
}


// Funcion para paginacion AJAX de Suministros
function pageSupplies(element) {
    const page = element.getAttribute('data-page');
    // Obtener valores de filtro actuales del formulario
    const filterForm = document.getElementById('filter-form-supply');
    const formData = filterForm ? new FormData(filterForm) : new FormData();

    // Asegurar que el parámetro 'page' correcto se envíe
    formData.set('page', page); // set sobrescribe si ya existe

    const contentDiv = document.getElementById('supply-list-content');
    if (!contentDiv) {
        console.error("Element with ID 'supply-list-content' not found.");
        swalAgrow.fire('Error', 'Error interno: Contenedor de tabla no encontrado.', 'error');
        return;
    }
    contentDiv.style.opacity = '0.5'; // Indicate loading

    // El endpoint correcto según SupplyController es /supplies/table
    const url = `/supplies/table?${new URLSearchParams(formData).toString()}`;

    fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.text();
        })
        .then(html => {
            // Reemplazar el contenido - El fragmento /supplies/table ya incluye tabla y paginación
            contentDiv.innerHTML = html;
            // Re-inicializar JS necesario para el nuevo contenido
            formatPrices();
            attachConfirmHandlers();
            initFlatpickr(); // Si aplica
        })
        .catch(error => {
            console.error('Error fetching supply page:', error);
            swalAgrow.fire('Error', 'No se pudo cargar la página de suministros solicitada.', 'error');
            contentDiv.innerHTML = '<p class="info-card">Error al cargar los datos. Intente de nuevo.</p>';
        })
        .finally(() => {
            contentDiv.style.opacity = '1'; // Restore opacity
        });
}


