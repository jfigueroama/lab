(require '[clojure.core.async :as async :refer [<!! <! go-loop]] :reload)
(use 'firmata.core)
(require '[firmata.async :refer [analog-event-chan]])
(def board (open-serial-board "/dev/ttyACM0"))

(let [ch    (event-channel board)
      _     (query-firmware board)
      event (<!! ch)]
  (pprint event))
;  (is (= :firmware-report (:type event)))
;  (is (= "2.3" (:version event)))
;  (is (= "Firmware Name" (:name event))))

(-> board
      (set-pin-mode 16 :input)
      (enable-analog-in-reporting 0 true))
(let [ch    (event-channel board)
      event (<!! ch)]
  (pprint (:value event)))

(let [ch (analog-event-chan board 0)]
  (go-loop [evt (<! ch)]
           (pprint evt)))

(close! board)
