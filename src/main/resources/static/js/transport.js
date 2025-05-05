document.addEventListener('DOMContentLoaded', function() {
    const tableContainer = document.getElementById("transport-list-content");
    
    // Función de filtrado mejorada
    function filterTransport(estado = null, destino = "") {
        if (!tableContainer) return;

        tableContainer.style.opacity = '0.5';
        tableContainer.innerHTML = '<div class="text-center p-3"><i class="fa fa-spinner fa-spin"></i> Cargando...</div>';

        const params = new URLSearchParams();
        if (estado !== null) params.append("estado", estado);
        if (destino) params.append("destino", destino);

        fetch(`/transport/filtrar?${params.toString()}`, {
            headers: {'X-Requested-With': 'XMLHttpRequest'}
        })
        .then(response => {
            if (!response.ok) throw new Error("Error en la respuesta");
            return response.text();
        })
        .then(html => {
            tableContainer.innerHTML = html;
            updateActiveButtons(estado);
        })
        .catch(error => {
            tableContainer.innerHTML = '<div class="alert alert-danger">Error al cargar datos</div>';
            console.error("Error:", error);
        })
        .finally(() => {
            tableContainer.style.opacity = '1';
        });
    }

    // Actualizar botones activos
    function updateActiveButtons(estado) {
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.classList.remove('active');
        });

        if (estado === true) {
            document.getElementById('filterEnCamino').classList.add('active');
        } else if (estado === false) {
            document.getElementById('filterEntregado').classList.add('active');
        }
    }

    // Eventos
    document.getElementById('filterEnCamino')?.addEventListener('click', (e) => {
        e.preventDefault();
        filterTransport(true);
    });

    document.getElementById('filterEntregado')?.addEventListener('click', (e) => {
        e.preventDefault();
        filterTransport(false);
    });

    document.getElementById('refreshButton')?.addEventListener('click', (e) => {
        e.preventDefault();
        document.getElementById('destinoFilter').value = '';
        filterTransport();
    });

    document.getElementById('searchButton')?.addEventListener('click', (e) => {
        e.preventDefault();
        const destino = document.getElementById('destinoFilter').value;
        filterTransport(null, destino);
    });

    // Búsqueda al escribir con debounce
    let timeout;
    document.getElementById('destinoFilter')?.addEventListener('input', function() {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            filterTransport(null, this.value);
        }, 500);
    });
});