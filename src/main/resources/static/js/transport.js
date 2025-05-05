document.addEventListener('DOMContentLoaded', function() {
    const FILTERS = {
        EN_CAMINO: 'true',
        ENTREGADO: 'false',
        TODOS: null
    };

    // Elementos del DOM
    const elements = {
        tableContainer: document.getElementById("transport-list-content"),
        filterEnCamino: document.getElementById("filterEnCamino"),
        filterEntregado: document.getElementById("filterEntregado"),
        refreshButton: document.getElementById("refreshButton"),
        destinoFilter: document.getElementById("destinoFilter"),
        searchButton: document.getElementById("searchButton")
    };

    // Estado actual
    let currentState = {
        estado: null,
        destino: null,
        page: 0
    };

    // Función para mostrar carga
    function showLoading() {
        elements.tableContainer.innerHTML = `
            <tr>
                <td colspan="11" class="text-center py-4">
                    <i class="fas fa-spinner fa-spin me-2"></i>
                    Cargando transportes...
                </td>
            </tr>
        `;
        elements.tableContainer.style.opacity = '0.7';
    }

    // Función para aplicar filtros
    async function applyFilters() {
        showLoading();
        
        const params = new URLSearchParams();
        if (currentState.estado !== null) params.append("estado", currentState.estado);
        if (currentState.destino) params.append("destino", currentState.destino);
        params.append("page", currentState.page);

        try {
            const response = await fetch(`/transport/filtrar?${params.toString()}`, {
                headers: {'X-Requested-With': 'XMLHttpRequest'}
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const html = await response.text();
            elements.tableContainer.innerHTML = html;
            elements.tableContainer.style.opacity = '1';
            updateActiveButtons();
            setupPaginationEvents();
        } catch (error) {
            console.error("Error al filtrar:", error);
            elements.tableContainer.innerHTML = `
                <tr>
                    <td colspan="11" class="text-center text-danger py-4">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Error al cargar los transportes
                    </td>
                </tr>
            `;
            elements.tableContainer.style.opacity = '1';
        }
    }

    // Actualizar botones activos
    function updateActiveButtons() {
        if (elements.filterEnCamino && elements.filterEntregado) {
            // Resetear ambos
            elements.filterEnCamino.classList.remove('btn-success');
            elements.filterEnCamino.classList.add('btn-outline-success');
            elements.filterEntregado.classList.remove('btn-danger');
            elements.filterEntregado.classList.add('btn-outline-danger');
            
            // Activar el correspondiente
            if (currentState.estado === FILTERS.EN_CAMINO) {
                elements.filterEnCamino.classList.remove('btn-outline-success');
                elements.filterEnCamino.classList.add('btn-success');
            } else if (currentState.estado === FILTERS.ENTREGADO) {
                elements.filterEntregado.classList.remove('btn-outline-danger');
                elements.filterEntregado.classList.add('btn-danger');
            }
        }
    }

    // Configurar eventos de paginación
    function setupPaginationEvents() {
        document.querySelectorAll('.pagination a[data-page]').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                currentState.page = link.getAttribute('data-page');
                applyFilters();
            });
        });
    }

    // Inicializar eventos
    function initializeEvents() {
        // Botón En Camino
        elements.filterEnCamino?.addEventListener('click', (e) => {
            e.preventDefault();
            currentState.estado = FILTERS.EN_CAMINO;
            currentState.destino = null;
            currentState.page = 0;
            elements.destinoFilter.value = '';
            applyFilters();
        });

        // Botón Entregado
        elements.filterEntregado?.addEventListener('click', (e) => {
            e.preventDefault();
            currentState.estado = FILTERS.ENTREGADO;
            currentState.destino = null;
            currentState.page = 0;
            elements.destinoFilter.value = '';
            applyFilters();
        });

        // Botón Mostrar Todos
        elements.refreshButton?.addEventListener('click', (e) => {
            e.preventDefault();
            currentState.estado = FILTERS.TODOS;
            currentState.destino = null;
            currentState.page = 0;
            elements.destinoFilter.value = '';
            applyFilters();
        });

        // Botón Buscar
        elements.searchButton?.addEventListener('click', (e) => {
            e.preventDefault();
            currentState.estado = FILTERS.TODOS;
            currentState.destino = elements.destinoFilter.value;
            currentState.page = 0;
            applyFilters();
        });

        // Búsqueda al escribir
        let timeout;
        elements.destinoFilter?.addEventListener('input', () => {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                currentState.estado = FILTERS.TODOS;
                currentState.destino = elements.destinoFilter.value;
                currentState.page = 0;
                applyFilters();
            }, 500);
        });
    }

    // Inicializar
    initializeEvents();
    applyFilters(); // Carga inicial
});