(ns server.core
  (:require [com.stuartsierra.component :as component]
            [server.components.app  :as appns])
  (:gen-class))


(defn app
  [config]
  (component/system-using
    (appns/new-app config)
    {:handler [:db :comm]
     :webserver [:handler]
     :comm [:db]}))

(defn -main
  []
  (let [config (read-string (slurp "config.edn"))
        system (app config)]
    (do
      (component/start system))))



