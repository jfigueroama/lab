(ns cards.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [cards.svg :as svg]
            [devtools.core :as devtools]))

(defn dev-setup [mode]
  (enable-console-print!)
  (println (str mode " mode"))
  (devtools/install!))

;(defn mount-root []
;  (if-let [app (.getElementById js/document "app")]
;    (reagent/render [views/main-panel] app)))

(defn ^:export init []
 ; (if-let [app (.getElementById js/document "app")]
 ;   (let [hid (.getAttribute app "data-id")
 ;         mode (.getAttribute app "data-mode")]
 (do
   (dev-setup "cards")))

(init)
