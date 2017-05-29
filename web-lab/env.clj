(require '[watch.core :as w :refer [stop-watch reload]])
(require '[clojure.test :as t])
(require '[clojure.core.async :as a])

(require '[cljs.core.async.macros :as ama :refer [go go-loop]])
(require '[cljs.core.async :as a ])


(def obs (reload '[metah.core :as mh] "./src/metah/core.clj"
                 (t/run-tests)))

(def c (a/chan))
(def flag (atom true))
(def counter (atom 0N))


(defn iterator
  "Funcion iteradora de la metaheuristica.
  Recibe un canal y los datos de inicio de la metaheuristica.
  Simplemente mete los datos al canal con la iteracion incrementada."
  [ichan state]
  (do
    (a/go (a/>! ichan (update state :iteration inc)))
    ichan))


(defn iterar
  "Itera una funcion metaheuristica si es que la flag es true con los
  datos que salen del canal."
  [mhfn astate flag ichan]
  (a/go-loop []
    (let [state (a/<! ichan)]
      (if @flag
        (do
          (mhfn state)
          (recur))
        (reset! astate state)))))


(defn avisadora
  [sol]
  (swap! counter inc))

(defn metah-ejemplo
  "Metaheuristica de ejempo.
  Recibe el state y lo devuelve actualizado."
  [state]
  (do
    (reduce + (range 1 1000000000))
    (update state :state inc)))


(defn metaheuristica
  [real-metah state]
  (let [itfn (:iterator-fn state)
        avfn (:advisor-fn state)]
    (do
      (let [nstate (real-metah state)]
        (avfn (:state nstate))
        (itfn nstate)))))

(def init-state
  (atom {:iteration 0N
   :bounds []
   :state 1N
   :params {}
   :iterator-fn (partial iterator c)
   :advisor-fn avisadora
   }))

(a/go (a/>! c @init-state))

(iterar (partial metaheuristica metah-ejemplo) init-state flag c)

(a/go (metaheuristica (a/<! c)))

(a/go (let [s (a/<! c)]
        (println "state: " s)))

(reset! flag false)
(reset! flag true)
(println @counter)
(println @init-state)


