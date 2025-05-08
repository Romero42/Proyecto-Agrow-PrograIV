//actualizar paginacion en listAlquiler
function pageRent(element){

    var tableCurrent = document.getElementById("tableData");
    var currentPage = element.getAttribute('data-page');

    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {

            tableCurrent.innerHTML = this.responseText;
            attachConfirmHandlers();
        }
    };

    xhttp.open("GET", "/rent/pageCurrent?page=" + currentPage, true);
    xhttp.send();
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
            attachConfirmHandlers();
        }
    };

    xhttp.open("GET", "/rent/tableFilter?" + params, true);
    xhttp.send();
}

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

    function cerrarInfoMaquina() {
        document.getElementById("infoMaquina").innerHTML = "";
    }

    function viewMaquina(element){

        var maquinaInfo = document.getElementById("infoMaquina");
        var id_maquina = element.getAttribute('data-id');

        var xhttp = new XMLHttpRequest();

        xhttp.onreadystatechange = function () {
            if (xhttp.readyState === 4 && xhttp.status === 200) {

                maquinaInfo.innerHTML = this.responseText;
                 formatPrices();

            }
        };

        xhttp.open("GET", "/rent/viewMaquina?id_maquina=" + id_maquina, true);
        xhttp.send();
    }