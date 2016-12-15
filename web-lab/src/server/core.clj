(ns server.core
  (:require [compojure.route :only [files not-found]]
            [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
            [compojure.core :only [defroutes GET POST DELETE ANY PUT context]]
            org.httpkit.server))

(defonce webserver (atom nil))


(defroutes app
  (GET "/" [] "<h1>Servidor de Optimizaci&oacute;n</h1> Mostrar ayuda pronto!!")

  ;; SERVIDOR
  ; Parametros actuales
  (GET "/params" []
            (json/write-str @*params*))

  (GET "/param/:pa" [pa]
       (json/write-str (get @*params* (keyword pa))))

  (POST "/params/:param/:valor" [param valor]
        (let [kpa (keyword param)]
          (json/write-str
            (if (contains? @*params* kpa)
              (swap! *params* assoc kpa valor)
              nil))))

  ;; PROCESOS
  ; Lista de procesos.
  (GET "/procesos" []
       (json/write-str @*procs* :value-fn value-fn-dates))

  ; Alta de un proceso, retorna un uid unico y otros datos del proceso..
  (GET "/proceso-new" [data]
       (json/write-str
          (seop/process-create (json/read-str data))))
  ; Estado de un proceso (cambios o nuevos resultados).
  (GET "/proceso/:pid" [pid] (str "Estado del proceso " pid))
  ; Detener un proceso. Los procesos no se borran.
  (DELETE "/proceso/:pid" [pid] (str "Deteniendo el proceso " pid))
  ; Cambiar los datos de un proceso
  (POST "/proceso/:pid" [pid] (str "Cambiando el proceso " pid " con variables POST"))
  (POST "/eliminar/:pid" [pid] (str "Borrar todos los valores del proceso " pid))
  (GET "/schedule" []
        (str "<html><head></head><body>
        <form id='f1' method='POST' action='/schedule'>
       Data:<br/><textarea name='data'></textarea><br/>
       <input type='submit' value='enviar' /></form>
       <br/><br/>
       </body></html>
       "))
  (POST "/schedule" [data]
        (str "datos:" data))
  (GET "/genpid" [] (str "Pid: " (seop/gen-pid)))

  ; Not found
  (not-found "<h1>P&aacute;gina no encontrada.</h1>"))



;;; WEB SERVER ;;;


(defn stop-server
  []
  (when-not (nil? @webserver)
    ;; espera 10000ms para que terminen las peticiones pendientes.
    (@webserver :timeout 10000)
    (reset! webserver nil)
    (reset! *runners-flag* false)
    (seop/dispatcher-server-stop)
    (reset! *runners* nil)
    ))

(defn start-server
  "Los parms son para cambiar puerto y esas cosas."
  ([]
   (start-server {}))
  ([parms]
   (do
     (reset! webserver (run-server (site #'app) {:port 8080}))
     (reset! *runners-flag* true)
     (reset! *runners* (async/chan (:max-processes @*params*)))
     (seop/init-vars)
     ;(enqueue-procs runners (processes))
     (seop/dispatcher-server-start)
     )))

(defn -main
  ([]
   ;; La variable 'app' esta en seo.routes.
   ;;
   ;; The #' is useful, when you want to hot-reload code
   ;; You may want to take a look: https://github.com/clojure/tools.namespace
   ;; and http://http-kit.org/migration.html#reload
   ;;
     (start-server))
  ([&args]
   (-main {})))

(defn -main
  "I don't do a whole lot."
  [& args]
  )
