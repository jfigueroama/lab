(require '[clojure.java.jdbc :as j])

(def conn {:dbtype "postgresql"
           :dbname "lwbdt"
           :host "localhost"
           :user "jfigueroa"
           :password "jfigueroa"
           :ssl true
           :sslfactory "org.postgresql.ssl.NonValidatingFactory"})


;;; SOBRE 



(j/query conn ["select * from person limit 10"])
(j/query conn ["select * from person limit 0"])
(j/query conn ["select * from person limit 1" 3423])
(j/query conn ["select * from person limit ?" "cadena"])
(j/query conn ["select * from person limit hola "])
(j/query conn ["select * from persoan limit 1 "])

(with-transaction
  (->= (get-horario 10)
       get-data
       format-data
       include-report)) ???

(worker
  [flag ch state]
  (let [
  (while @flag
    (let [x (a/<!! ch)]
      (->= (toperson x)         ; lo convierte a persona
           find-partner         ; devuelve la pareja de la persona la cual esta en la db
           process-action       ; procesa la pareja
           report-state))))     ; recibe una accion como :added :updated y cambia el state
)

(defn hola
  ([a]
   (str "Hola con 1 argumento: " a))
  ([a b]
   (str "Hola con 2 argumentos: " a ", " b)))

((partial hola) 1)
(partial hola 1)
((partial hola 1))
((partial hola 1) 2)


