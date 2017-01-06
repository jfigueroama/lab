(ns server.utils
  "")

(defn rand-color
  []  (let [values ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9" "A" "B" "C" "D" "E" "F"]
        tvalues (fn [vs] (str (rand-nth vs) (rand-nth vs)))]
    (apply str (concat ["#"] (repeatedly 3 (partial tvalues values))))))

(defn rand-point
  [id]
  {:id id
   :x (rand-int 800)
   :y (rand-int 600)
   :r (inc (rand-int 10))
   :c (rand-color)
   :sw (rand-int 3)
   :fc (rand-color)})

