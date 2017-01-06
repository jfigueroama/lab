(ns server.components.handler
  "Define el componente webapp como las rutas y handlers de la aplicacion web.
  Requiere de la base de datos para integrar dicha info a los handlers."
  (:require [com.stuartsierra.component :as component]
            [server.handlers :as h]
            [compojure.core :refer :all]
            [compojure.route :as route])
  (:use ring.middleware.params
        ring.middleware.keyword-params
        ring.middleware.multipart-params
        ;ring.middleware.cookies
        ring.middleware.session
        ring.util.response))

(defn app-routes
  [conf comm dbc]
  (routes
    (GET "/" request h/root-handler)

    ;(GET  "/socket-horarios" req ((:rag comm)  req))
    ;(POST "/socket-horarios" req ((:rap comm)  req))

    (GET "/test"
         req
         (fn test-hn [req]
           "hola chicos feliz anio :0)"))

    (GET "/ui/cards" req h/cards-handler)
    
    (GET "/rand/point" req h/rand-point-hl)

    (route/files "/public/")))
     ; TODO incluir files


(defrecord Handler
  [config handler comm db]
  component/Lifecycle
  (start [component]
    (println ";; Starting Handler")
    (let [handler (-> (app-routes config
                                  (:comm component)
                                  (:db component))
                      wrap-keyword-params
                      wrap-params
                      wrap-session
                      wrap-multipart-params
                      ;wrap-cookies
                      )]
      (assoc component :handler handler)))
  (stop [component]
    (println ";; Stopping Handler")
    (assoc component :handler nil)))

(defn new-handler
  [conf]
  (map->Handler {:config conf}))
