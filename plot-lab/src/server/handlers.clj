(ns server.handlers
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [cognitect.transit :as transit]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [server.utils :refer [rand-point]] )
  (:use ring.util.response)
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))



(defn jresponse
  [data & ops]
  (-> (response (json/generate-string 
                  data 
                  {:pretty true :escape-non-ascii true}))
      (content-type "application/json")
      (header "Access-Control-Allow-Origin" "*")
      (header "Access-Control-Allow-Headers" "Content-Type") 
      (charset "UTF-8") ))

(defn tresponse
  "Retorna una response con transit."
  [data & ops]
  (let [out (ByteArrayOutputStream.)
        w   (transit/writer out :json)
        _ (transit/write w data)
        ret (.toString out "UTF-8") ]
    (.reset out)
    (-> (response ret)
        (content-type "application/transit+json;")
              (header "Access-Control-Allow-Origin" "*")
      (header "Access-Control-Allow-Headers" "Content-Type")
      (charset "UTF-8") )))

(defn root-handler
  [req]
  (str "<h1>Test de ploteo </h1>"))

(defn cards-handler
  [req]
  (html5 [:head
          [:meta { :http-equiv "content-type"
                  :content "text/html; charset=UTF-8"}]
          [:meta {:charset="UTF-8"}]
          (include-css "/public/css/cards.css")
          [:title "Cards para pruebas de visualizacion"] ]
         [:body
          [:script {:src (str "/public/js/cards.js")} ]] ))

(defn rand-point-hl
  [req]
  (let [nn (str (-> req :params :n))
        n (or (if (empty? nn) 1 (Long. nn))  1)]
    (tresponse
      (map rand-point (range n)))))

