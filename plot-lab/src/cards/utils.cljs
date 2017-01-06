(ns cards.utils
  (:require [cognitect.transit :as transit]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]))

(defn log
  [& data]
  (apply (partial (.-log js/console)) data))

(defn info
  [& data]
  (apply (partial (.-info js/console)) data))

(defn read-transit
  [tdata]
  (let [r (transit/reader :json)]
    (transit/read r tdata)))


(defn get-transit
  [url hfn]
  (GET url
       {:handler (fn load-transit-handler
                   [r]
                   (let [rt (read-transit r)]
                     (hfn rt)))
        :error-handler (fn load-transit-ehandler
                         [status status-text]
                         (.log js/console
                               (str "something bad happened: "
                                    status " " status-text)))
        :response-format :raw}))

