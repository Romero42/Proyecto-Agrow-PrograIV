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
            attachConfirmHandlers();
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