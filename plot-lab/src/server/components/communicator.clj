(ns server.components.communicator
  "Componente para el servidor que espera los mensajes del cliente, guarda los
  cambios y replica dichos cambios a todos los clientes."

  (:require [com.stuartsierra.component :as component]

            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit      :refer (get-sch-adapter)]
            [clojure.core.async :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))


(defn communicator-handler
  "Maneja los mensajes llegados de la siguiente manera:
   Guarda las instancias en la base de datos y las remanda por el socket a
   todos los clientes con la send-fn."
  [dbc send-fn {:keys [event client-id uid]}]
  (let [[ev-id data] event]
    nil))


(defrecord Communicator [db
                         flag
                         router
                         rap ;ring-ajax-post
                         rag ;ring-ajax-get-or-ws-handshake
                         ch-chsk
                         chsk-send!
                         connected-uuids]

 component/Lifecycle

 (start [component]
   (println ";; Starting Communicator")

   (let [{:keys [ch-recv send-fn connected-uids
                 ajax-post-fn ajax-get-or-ws-handshake-fn]}
         (sente/make-channel-socket! (get-sch-adapter) {})
         component    (assoc component
                       :flag (atom true)
                       :rap ajax-post-fn
                       :rag ajax-get-or-ws-handshake-fn
                       :ch-chsk  ch-recv               ; ChannelSocket's receive channel
                       :chsk-send! send-fn             ; ChannelSocket's send API fn
                       :connected-uids connected-uids)] ; Watchable, read-only atom

     (assoc component
            :flag (reset! (:flag component) false)
            :router (sente/start-server-chsk-router!
                      ch-recv
                      (partial communicator-handler
                               (:db component)
                               send-fn)))))

 (stop [component]
   (println ";; Stopping Communicator")
   (if-let [stop-f router]
     (assoc component :router (stop-f))
     component)))


(defn new-communicator
  []
  (map->Communicator {}))

