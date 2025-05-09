//ventana de mensaje del tipo que se obtiene
function mostrarAlerta(tipo, mensaje) {
    Swal.fire({
        icon: tipo, // success, error, warning, info, question
        text: mensaje,
        title: '¡Error!',
        timer: 3500,
        timerProgressBar: true,
        confirmButtonText: 'OK'
    });
}

function filtrarUsuarios(element){

    var tableUpdate = document.getElementById("table-user");
    var user = document.getElementById("username").value;
    var type = document.getElementById("tipo").value;
    var currentPage = element.getAttribute('data-page');
    var button = element.getAttribute('data-button');
    var previewType = document.getElementById("previewType").value;
    var params = "page=" + encodeURIComponent(currentPage);
    var xhttp = new XMLHttpRequest();

    if(user && type.trim() && button == 0){

        mostrarAlerta("error", "Use solo un filtro");
        return;
    }

    if(!user && !type.trim() && button == 0){

        mostrarAlerta("error", "Use un filtro");
        return;
    }

    if(user && button == 0){

        params = "user=" + encodeURIComponent(user)
        + "&page=" + encodeURIComponent(currentPage);
    }else if(type.trim() && button == 0){

        previewType = type;
        params = "type=" + encodeURIComponent(type)
            + "&page=" + encodeURIComponent(currentPage);
    }else if(previewType != "none" && button ==1 || user && previewType != "none" && button ==1){

        params = "type=" + encodeURIComponent(previewType)
                    + "&page=" + encodeURIComponent(currentPage);
    }else{
        params = "page=" + encodeURIComponent(currentPage);
    }

     xhttp.onreadystatechange = function () {
            if (xhttp.readyState === 4 && xhttp.status === 200) {

                tableUpdate.innerHTML = this.responseText;
            }
        };

     xhttp.open("GET", "/user/tableFilter?" + params, true);
     xhttp.send();
}