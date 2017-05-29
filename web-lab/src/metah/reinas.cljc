(ns metah.reinas)

(defn bounds
  [n]
  {:pre [(pos-int? n)]}
  (vec (repeat n (vec (range 0 n)))))

(defn rand-sol
  [bounds]
  (mapv rand-nth bounds))

(defn ataque-individual?
  [[x y] [w z]]
  (or (= x w)
      (= y z)
      (= (Math/abs (- x w))
         (Math/abs (- y z)))))


(defn ataque?
  ([sol]
   (ataque? sol (count sol) 0 0 1))
  ([sol cuantas ataques dori uke]
   (if (<


(bounds 10)
(bounds -10)
(rand-sol (bounds 10))

