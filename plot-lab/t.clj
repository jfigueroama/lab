(in-ns 'user)
(load-file "env.clj")


; Testing y otras cosas SI

;(pprint (q dbc "select * from profesor limit 1"))


{:algo
   :foo otra
   :cosa 1}
[1
 2
 3
 4]
;;;;;;;;;;;;;;
; Tomates csv
;(require '[clojure.data.csv :as csv]
;                  '[clojure.java.io :as io])

(use 'clojure-csv.core)
(use '(incanter core charts excel datasets stats io bayes) :reload)
(require '[com.hypirion.clj-xchart :as c])


(def data (get-dataset :airline-passengers))
(def data (read-dataset "tomates.csv" :header true))
(def iris (get-dataset :iris))

(doto (box-plot (sample-gamma 1000 :shape 1 :rate 2)
                :legend true :y-label "")
  view 
  (add-box-plot (sample-gamma 1000 :shape 2 :rate 2))
  (add-box-plot (sample-gamma 1000 :shape 3 :rate 2)))


(doto (box-plot (sample-normal 1000) 
                :title "Normal Boxplot"
                :legend true)
  (add-box-plot (sample-normal 1000 :sd (sqrt 0.2)))
  (add-box-plot (sample-normal 1000 :sd (sqrt 5.0)))
  (add-box-plot (sample-normal 1000 :mean -2 :sd (sqrt 0.5)))
  view)

(doto (box-plot (sample-beta 1000 :alpha 1 :beta 1) 
                :title "Beta Boxplot"
                :legend true)
  (add-box-plot (sample-beta 1000 :alpha 5 :beta 1)) 
  (add-box-plot (sample-beta 1000 :alpha 1 :beta 3))
  (add-box-plot (sample-beta 1000 :alpha 2 :beta 2))
  (add-box-plot (sample-beta 1000 :alpha 2 :beta 5))
  view)



;;;;;;;;;;;;;;;;

(def tomates (parse-csv (slurp "tomates.csv")))
(def tomas (map (fn [x]
                  {:planta (Integer/parseInt (get x 0))
                   :fecha  (get x 1)
                   :peso  (Double. (get x 2))})
                (rest tomates)))

(view tomas)
(view (histogram [1 2 3 5 6  2 3  5 1 0]))
(view (scatter-plot :Sepal.Length :Sepal.Width :data [ 1 2 3 4 5 1 2 3 4 5]))
(view (histogram tomas))


(c/view
    (c/pie-chart tomas {:title "Tomates"}))


;;;;;;;;;;;;;;;;;;;
(def data
  (dataset ["x1" "x2" "x3"] 
           [[1 2 3] 
            [4 5 6] 
            [7 8 9]]))

(view (scatter-plot :Sepal.Length :Sepal.Width 
                                        :data (get-dataset :iris)))

(view (scatter-plot :Sepal.Length :Sepal.Width 
                    :data (get-dataset :iris)
                    :group-by :Species
                    ))

(with-data (get-dataset :iris)
  (doto (scatter-plot :Petal.Width :Petal.Length
                      :data ($where {"Petal.Length" {:lte 2.0} 
                                     "Petal.Width" {:lt 0.75}}))
    view))
