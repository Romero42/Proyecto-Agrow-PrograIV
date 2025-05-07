function getCurrentRentFilters() {
    const form = document.getElementById('filter-form-rent');
    const params = new URLSearchParams();
    if (!form) return params;

    const start = form.querySelector('#rentStartDay')?.value;
    if (start) params.append('rentStartDay', start);

    const end = form.querySelector('#rentFinalDay')?.value;
    if (end) params.append('rentFinalDay', end);

    const id = form.querySelector('#id_maquina')?.value;
    if (id) params.append('id_maquina', id);
    return params;
}

function viewMaquina(link) {
    if (!link) return;
    const machineId = link.getAttribute('data-id');
    const infoDiv = document.getElementById('infoMaquina');
    if (infoDiv && machineId) {
        handleAjaxRequest(`/rent/viewMaquina?id_maquina=${machineId}`, 'infoMaquina');
    }
}

function cerrarInfoMaquina() {
    const infoDiv = document.getElementById('infoMaquina');
    if (infoDiv) infoDiv.innerHTML = "";
}


document.addEventListener('DOMContentLoaded', () => {

    const machineryRentalModuleConfig = {
        filterFormId: 'filter-form-rent',
        tableContentId: 'tableData', // ID del div que contiene el contenido de la tabla
        defaultPaginateUrl: '/rent/pageCurrent', // URL base para paginación sin filtros
        // Manejador personalizado de envío del formulario de filtro debido a validaciones específicas
        customFilterSubmitHandler: function(form) {
            const params = new URLSearchParams(new FormData(form));
            params.set('page', '0'); // Reiniciar a página 0 al filtrar

            const start = params.get('rentStartDay');
            const end = params.get('rentFinalDay');
            const id_maquina = params.get('id_maquina');

            if ((start && id_maquina) || (end && id_maquina) || (start && end && id_maquina)) {
                swalAgrow.fire("Error", "Use solo un tipo de filtro a la vez (rango de fechas o código de máquina).", "error");
                return;
            }
            if (start && end && start > end) {
                swalAgrow.fire("Error", "La fecha de inicio no puede ser posterior a la fecha final.", "error");
                return;
            }
            if (!start && !end && !id_maquina) {
                swalAgrow.fire("Info", "Por favor ingrese un filtro para buscar.", "info");
                return;
            }
            // Realizar la petición con handleAjaxRequest
            handleAjaxRequest(`/rent/tableFilter?${params.toString()}`, machineryRentalModuleConfig.tableContentId);
        },
        // Manejador personalizado de paginación para este módulo
        customPaginationHandler: function(link) {
            const page = link.getAttribute('data-page');
            const params = getCurrentRentFilters(); // Obtener filtros actuales
            params.set('page', page);

            const hasFilters = params.has('rentStartDay') || params.has('rentFinalDay') || params.has('id_maquina');
            const endpoint = hasFilters ? '/rent/tableFilter' : machineryRentalModuleConfig.defaultPaginateUrl;

            handleAjaxRequest(`${endpoint}?${params.toString()}`, machineryRentalModuleConfig.tableContentId);
        }
    };

    // Adjuntar listeners genéricos y usar handlers personalizados
    if (typeof attachGenericTableListeners === 'function') {
         attachGenericTableListeners(machineryRentalModuleConfig);
    } else {
        console.error("attachGenericTableListeners no está definido. Asegúrate de que default.js esté cargado.");
    }


    // Listener específico para el botón de filtro en machineryR.html
    const filterButton = document.querySelector('#filter-form-rent button[onclick^="filterRent"]');
    if (filterButton) {
        filterButton.removeAttribute('onclick'); // Eliminar handler inline antiguo
        filterButton.addEventListener('click', (event) => {
            event.preventDefault();
            const form = document.getElementById(machineryRentalModuleConfig.filterFormId);
            if (form && machineryRentalModuleConfig.customFilterSubmitHandler) {
                machineryRentalModuleConfig.customFilterSubmitHandler(form);
            }
        });
    }
    console.log("JS específico del módulo de alquiler de maquinaria cargado.");
});

window.viewMaquina = viewMaquina;
window.cerrarInfoMaquina = cerrarInfoMaquina;
