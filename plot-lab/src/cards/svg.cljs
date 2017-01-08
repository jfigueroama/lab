(ns cards.svg
  (:require   [reagent.core  :as reagent]
              [clojure.string :as string]
              [cards.utils :as utils])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-rg]]))


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
  (fn [state owner]
    [:span
     [:strong "Contador: " (:contador @state)]
     [:input {:value "+"
              :type "button"
              :on-click #(reset! state
                                 (update @state :contador inc))}]])
  (reagent/atom {:contador 0}))


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
                                        (swap! state #(assoc %
                                                             :points
                                                             (zipmap
                                                               (range (count tr))
                                                                tr)))
                                        (utils/info (str "Pintados "
                                                         (:n @state)
                                                         " puntos"))))))}
      "Pintar"]
    (into
      [:svg {:width "800" :height "600"}]
      (for [[k p] (:points @state)]
        ^{:key k}
        [:circle.point
         {:on-click #(utils/info "Clicleado " (-> % .-target .-id))
          :on-mouse-over
          (fn [ev]
            (let [o (.-target ev)
                  oid (long (.getAttribute o "data-id"))]
              (swap! state
                     (fn [x] (assoc x
                                    :points
                                    (dissoc (:points @state) oid))))))

          :id (str "point" k)
          :data-id k
          :cx (:x p) :cy (:y p) :r (:r p) :stroke (:c p)
          :stroke-width (:sw p)  :fill (:fc p)}]))])
  (reagent/atom {:n 100 :points {}}))
