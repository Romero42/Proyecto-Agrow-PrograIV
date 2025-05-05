document.addEventListener('DOMContentLoaded', () => {
    // [Todo tu código existente...]
    
    // ... hasta la última función:
    
    function pageHarvest(element) {
        const page = element.getAttribute('data-page');
        const state = element.getAttribute('data-state');
        const destiny = element.getAttribute('data-destiny');

        // Crear URL con parámetros de filtrado si existen
        let url = '/harvests/page?page=' + page;
        if (state && state !== '') {
            url += '&stateC=' + encodeURIComponent(state);
        }
        if (destiny && destiny !== '') {
            url += '&destinyC=' + encodeURIComponent(destiny);
        }

        console.log("Cargando página: " + url); // Para depuración

        // Hacer la solicitud AJAX
        fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error de red: ' + response.status);
                    }
                    return response.text();
                })
                .then(html => {
                    // Verificar si hay contenido
                    if (html.trim() === '') {
                        console.error('La respuesta está vacía');
                        return;
                    }

                    document.getElementById('harvest-list-content').innerHTML = html;

                    // Actualizar la clase active en la paginación
                    document.querySelectorAll('.pagination a').forEach(a => {
                        a.classList.remove('active');
                        if (a.getAttribute('data-page') === page) {
                            a.classList.add('active');
                        }
                    });

                    console.log('Página cargada correctamente');
                })
                .catch(error => {
                    console.error('Error cargando la página:', error);
                    // Mostrar mensaje al usuario
                    alert('Error al cargar la página: ' + error.message);
                });
    }
}); 