function getProducerFilters() {
    const form = document.getElementById('filter-form-producer');
    const params = new URLSearchParams();
    if (!form) return params;

    const city = form.querySelector('#city')?.value;
    const id_producer = form.querySelector('#id_producer')?.value;

    if (id_producer) {
        params.append('id_producer', id_producer);

    } else if (city) {
        params.append('city', city);
    }
    return params;
}


document.addEventListener('DOMContentLoaded', () => {

    const producerModuleConfig = {
        filterFormId: 'filter-form-producer',
        tableContentId: 'table-producer',
        defaultPaginateUrl: '/producers/list', // URL base para peticiones AJAX
        customFilterSubmitHandler: function(form) {
            const formData = new FormData(form);
            const params = new URLSearchParams(formData);
            params.set('page', '0'); // Reiniciar a página 0 al filtrar

            const city = params.get('city');
            const id_producer = params.get('id_producer');

            if (city && id_producer) {
                swalAgrow.fire("Error", "Use solo un filtro a la vez (ciudad o ID).", "error");
                return;
            }
            if (!city && !id_producer) {

                console.log("No se especificó filtro para productores, mostrando todo o basado en la página.");
            }
            handleAjaxRequest(`${producerModuleConfig.defaultPaginateUrl}?${params.toString()}`, producerModuleConfig.tableContentId);
        },
        customPaginationHandler: function(link) {
            const page = link.getAttribute('data-page');
            const params = getProducerFilters(); // Obtener filtros actuales del formulario
            params.set('page', page);
            handleAjaxRequest(`${producerModuleConfig.defaultPaginateUrl}?${params.toString()}`, producerModuleConfig.tableContentId);
        }
    };

    if (typeof attachGenericTableListeners === 'function') {
        attachGenericTableListeners(producerModuleConfig);
    } else {
        console.error("attachGenericTableListeners is not defined. Ensure default.js is loaded.");
    }

    // Manejar el botón de filtro específico para productores si no usa envío de formulario
    const filterButton = document.querySelector('#filter-form-producer button[onclick^="producer"]');
    if (filterButton) {
        filterButton.removeAttribute('onclick'); // Eliminar el handler inline antiguo
        filterButton.addEventListener('click', (event) => {
            event.preventDefault();
            const form = document.getElementById(producerModuleConfig.filterFormId);
            if (form && producerModuleConfig.customFilterSubmitHandler) {
                producerModuleConfig.customFilterSubmitHandler(form);
            }
        });
    }

    console.log("Producer module specific JS loaded.");
});
