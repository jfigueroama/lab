(ns cards.svg
  (:require   [reagent.core  :as reagent]
              [clojure.string :as string]
              [cards.utils :as utils])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-rg]]))


(def state
  (reagent/atom
    {:contador 0}))


(def handlers
  {:assoc-in (fn assoc-in-hn
               [db [_ path value]]
               (assoc-in db path value))
   :inc (fn inc-hn
          [db _]
          (update-in db [:contador] inc))})


(defn dispatch
  [[ev & args]]
  (if-let [hn (get handlers ev)]
    (do
      (reset! state (hn @state [ev args]))
      (utils/log ev))))


(defcard-rg primera
  "Some docs"
  (fn [contador owner]
    [:span
     [:strong "Contador: " @contador]
     [:input {:value "+"
              :type "button"
              :on-click #(dispatch [:inc])}]])
  (reagent/atom (:contador @state)))


(defcard-rg grafica-svg
  "Ejemplo de una grafica SVG con reagent."
  (fn [state _]
    [:div
     [:input {:type "text"
              :value (:n @state)
              :on-change
              (fn ptc-hn [ev]
                (let [n (-> ev .-target .-value)]
                  (reset! state
                          (assoc @state
                                :n n))))}]
     [:button {:on-click
               (fn [ev]
                 (utils/get-transit (str "/rand/point?n=" (:n @state))
                                    (fn gthn [tr]
                                      (do
                                        (swap! state #(assoc % :points tr))
                                        (utils/info (str "Pintados " (:n @state) " puntos"))))))}
      "Pintar"]
    (into
      [:svg {:width "800" :height "600"}]
      (for [p (:points @state)]
        ^{:key (:id p)}
        [:circle.point
         {:on-click #(utils/info "Clicleado " (-> % .-target .-id))
          :id (str "point" (:id p))
          :cx (:x p) :cy (:y p) :r (:r p) :stroke (:c p)
          :stroke-width (:sw p)  :fill (:fc p)}]))])
  (reagent/atom {:n 100 :points []}))
