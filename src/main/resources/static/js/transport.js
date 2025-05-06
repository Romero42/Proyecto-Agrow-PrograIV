document.addEventListener('DOMContentLoaded', function() {
    const FILTERS = {
        EN_CAMINO: true,
        ENTREGADO: false,
        TODOS: null
    };

    const elements = {
        tableContainer: document.getElementById("transport-list-content"),
        filterEnCamino: document.getElementById("filterEnCamino"),
        filterEntregado: document.getElementById("filterEntregado"),
        refreshButton: document.getElementById("refreshButton"),
        destinoFilter: document.getElementById("destinoFilter"),
        searchButton: document.getElementById("searchButton")
    };

    let currentState = {
        estado: FILTERS.TODOS,
        destino: elements.destinoFilter ? elements.destinoFilter.value : "",
        page: 0
    };

    function showLoading() {
        if (elements.tableContainer) {
            elements.tableContainer.innerHTML = `
                <tr>
                    <td colspan="11" class="text-center py-4">
                        <i class="fas fa-spinner fa-spin me-2"></i>
                        Cargando transportes...
                    </td>
                </tr>`;
        }
    }

    async function applyFilters() {
        if (!elements.tableContainer) return;
        
        showLoading();
        
        const params = new URLSearchParams();
        if (currentState.estado !== null) params.append("estado", currentState.estado);
        if (currentState.destino) params.append("destino", currentState.destino);
        params.append("page", currentState.page);

        try {
            const response = await fetch(`/transport/filtrar?${params.toString()}`, {
                headers: {'X-Requested-With': 'XMLHttpRequest'}
            });

            if (!response.ok) throw new Error(`Error ${response.status}`);
            
            const html = await response.text();
            elements.tableContainer.innerHTML = html;
            updateActiveButtons();
            setupPaginationEvents();
            setupDeleteButtons();
            
        } catch (error) {
            console.error("Error al filtrar:", error);
            elements.tableContainer.innerHTML = `
                <tr>
                    <td colspan="11" class="text-center text-danger py-4">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Error al cargar los datos
                    </td>
                </tr>`;
        }
    }

    function updateActiveButtons() {
        if (elements.filterEnCamino && elements.filterEntregado) {
            elements.filterEnCamino.className = 'btn btn-outline-success';
            elements.filterEntregado.className = 'btn btn-outline-danger';

            if (currentState.estado === FILTERS.EN_CAMINO) {
                elements.filterEnCamino.className = 'btn btn-success';
            } else if (currentState.estado === FILTERS.ENTREGADO) {
                elements.filterEntregado.className = 'btn btn-danger';
            }
        }
    }

    function setupPaginationEvents() {
        const paginationLinks = document.querySelectorAll('.pagination a[data-page]');
        paginationLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = link.getAttribute('data-page');
                if (page) {
                    currentState.page = parseInt(page);
                    applyFilters();
                }
            });
        });
    }

    function setupDeleteButtons() {
        const deleteForms = document.querySelectorAll('.delete-form');
        deleteForms.forEach(form => {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                confirmDelete(this);
            });
        });
    }

    function confirmDelete(form) {
        Swal.fire({
            title: '¿Eliminar transporte?',
            text: 'Esta acción no se puede deshacer',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(form.action, {
                    method: 'POST',
                    body: new FormData(form),
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                }).then(response => {
                    if (response.ok) {
                        applyFilters();
                        showToast('Transporte eliminado', 'success');
                    } else {
                        showToast('Error al eliminar', 'error');
                    }
                });
            }
        });
    }

    function showToast(message, type) {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer);
                toast.addEventListener('mouseleave', Swal.resumeTimer);
            }
        });
        
        Toast.fire({
            icon: type,
            title: message
        });
    }

    function initializeEvents() {
        if (elements.filterEnCamino) {
            elements.filterEnCamino.addEventListener('click', (e) => {
                e.preventDefault();
                currentState.estado = currentState.estado === FILTERS.EN_CAMINO ? FILTERS.TODOS : FILTERS.EN_CAMINO;
                currentState.page = 0;
                applyFilters();
            });
        }

        if (elements.filterEntregado) {
            elements.filterEntregado.addEventListener('click', (e) => {
                e.preventDefault();
                currentState.estado = currentState.estado === FILTERS.ENTREGADO ? FILTERS.TODOS : FILTERS.ENTREGADO;
                currentState.page = 0;
                applyFilters();
            });
        }

        if (elements.refreshButton) {
            elements.refreshButton.addEventListener('click', (e) => {
                e.preventDefault();
                currentState.estado = FILTERS.TODOS;
                currentState.destino = "";
                currentState.page = 0;
                if (elements.destinoFilter) elements.destinoFilter.value = "";
                applyFilters();
            });
        }

        if (elements.searchButton && elements.destinoFilter) {
            elements.searchButton.addEventListener('click', (e) => {
                e.preventDefault();
                currentState.destino = elements.destinoFilter.value.trim();
                currentState.page = 0;
                applyFilters();
            });

            let timeout;
            elements.destinoFilter.addEventListener('input', () => {
                clearTimeout(timeout);
                timeout = setTimeout(() => {
                    currentState.destino = elements.destinoFilter.value.trim();
                    currentState.page = 0;
                    applyFilters();
                }, 500);
            });
        }
    }

    if (elements.tableContainer) {
        initializeEvents();
        applyFilters();
    }
});