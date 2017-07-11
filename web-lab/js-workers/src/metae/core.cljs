(ns metae.core)
(enable-console-print!)

(def tiempo (atom 0))
(def actualizador (atom nil))

(def contando (atom false))
(def contador (atom 0))
(def contante (atom nil))

(defn contar
  []
  (swap! contador inc))


(defn handle
  [e]
  (.log js/self.console "hola mundo"))

(defn handle-message
  [e]
  (let [data (js->clj (.-data e))
        msg  (get data "msg")
        value (get data "value")]
    (case msg
      "tiempo"
      (do
        (if @actualizador
          (js/clearInterval  @actualizador))
        (reset! tiempo value)
        (reset! actualizador
                (js/setInterval
                  (fn actualizador-st
                    []
                    (if @contando
                      (.postMessage js/self @contador)))
                  @tiempo))
       (.log js/console "Tiempo handled" value) )
        
      "power"
      (do
        (if value
          (if (false? @contando)
            (reset! contante
                    (js/setInterval contar 1000)))
          (if (some? @contante)
            (do
              (js/clearInterval @contante)
              (reset! contante nil))))
        (reset! contando value)
        (.log js/console "power received" value)))))

(set! (.-onmessage js/self) handle-message)

; worker1.postMessage({"value": 3000, "msg": "tiempo"});
; worker1.postMessage({"msg": "power", "value": true});
; worker1.postMessage({"msg": "power", "value": false});
