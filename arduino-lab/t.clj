(use 'firmata.core)
(require '[clojure.core.async :as async :refer [<!!]])
(def board (open-serial-board "/dev/ttyACM0"))

(let [ch    (event-channel board)
      _     (query-firmware board)
      event (<!! ch)]
  (pprint event))
;  (is (= :firmware-report (:type event)))
;  (is (= "2.3" (:version event)))
;  (is (= "Firmware Name" (:name event))))

(set-digital board 3 :high)
(set-analog board 11 255)
