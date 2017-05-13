/**
 * Ejemplo de metaheuristica
 *
 *
 *
 */

var tiempo       = 0;
var actualizador = null;

var contando     = false;
var contador     = 0;
var contante     = null;

function contar(){
    contador = contador + 1;


    // cuando ya se tiene una solucion, hay que actualizar la variable que
    // tiene la mejor solucion a esa.
    //
    // cuando se actualizan los parametros hay que reiniciar la metaheuristica
}

onmessage = (e) => {
    var data = e.data;
    var msg  = data.msg;
    var val  = data.value;

    switch(msg){
        case "tiempo":
            if (actualizador)
                clearInterval(actualizador);
            tiempo = val;
            actualizador = setInterval(() => {
                if (contando){
                    self.postMessage(contador);
                }
            }, tiempo);
            break;

        case "power":
            if (val == false){ // apagar
                if (contante){
                    clearInterval(contante);
                    contante = null;
                }
            }else{ // prender
                if (contante == null){
                    contante = setInterval(() => {
                        contar();
                    }, 1000);
                }
            }
            contando = val;
            break;
    }

}

// worker.postMessage({"value": 3000, "msg": "tiempo"});
// worker.postMessage({"msg": "power", "value": true});
// worker.postMessage({"msg": "power", "value": false});
