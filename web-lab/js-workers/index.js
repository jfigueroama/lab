var worker1 = null;

window.addEventListener("load", (e) => {
    console.log("Iniciando worker");
    //worker = new Worker("mh1.js");
    worker1 = new Worker("out/main.js");

    worker1.onmessage = (e) => {
        console.log(e.data);
    };

});
