(ns db-lab.prestamos
  (:require [clojure.java.jdbc :as j :refer
             [with-db-connection with-db-transaction]]
            [db-lab.utils :refer [q exec insert]]))

(defn fecha-random-gen
  "recibe los siguientes valores:
  {:year (range 2016 2020)
   :month ....
  }"
  [p]
  (str (rand-nth (:year p)) "-" (rand-nth (:month p)) "-" (rand-nth (:day p))
       " "(rand-nth (:hour p)) ":" (rand-nth (:minute p)) ":" (rand-nth (:second p))))



(defn prestamo-inseguro
  [db uid monto re]
  (if-let [usu (first (q db "SELECT * from usuario WHERE id=:id" {:id uid}))]
    (do
      (exec db "UPDATE usuario SET deuda=deuda + :deuda WHERE id=:uid" {:uid uid :deuda monto})
      (insert db "INSERT INTO prestamo(usuario_id, monto, realizado_el) VALUES (:uid, :monto, :re)"
                 {:uid uid :monto monto :re re}))))

(defn pago-inseguro
  [db uid monto re]
  (if-let [usu (first (q db "SELECT * from usuario WHERE id=:id" {:id uid}))]
    (if (<= monto (:deuda usu 0))
      (do
        (exec db "UPDATE usuario SET deuda=deuda - :deuda WHERE id=:uid" {:uid uid :deuda monto})
        (insert db "INSERT INTO pago(usuario_id, monto, realizado_el) VALUES (:uid, :monto, :re)"
                   {:uid uid :monto monto :re re}))
      (throw (Exception. "Pago incoherente.")))))

(defn pago-seguro
  [db uid monto re]
  (try
    (exec db "START TRANSACTION")
    (let [id (pago-inseguro db uid monto re)]
      (exec db "COMMIT"))
    (catch Exception e
      (do
        (exec db "ROLLBACK")
        (throw e)))))

(defn operacion-segura
  [db ope]
  (try
    (exec db "START TRANSACTION")
    (let [id (ope)]
      (exec db "COMMIT"))
    (catch Exception e
      (do
        (exec db "ROLLBACK")
        (throw e)))))

(defn operacion-aleatoria
  [db uids frg]
  (let [uid (rand-nth uids)
        monto (inc (rand-int 100000))
        cual (rand-nth '(:pago :prestamo))
        re   (frg)
        ]
    (case cual
      :pago (partial prestamo-inseguro db uid monto re)
      :prestamo (partial pago-inseguro db uid monto re))))

(defn limpiar
  [cone]
  (with-db-connection [conn cone]
    (do
      (exec conn "DELETE FROM prestamo")
      (exec conn "DELETE FROM pago")
      (exec conn "UPDATE usuario SET deuda=0"))))

(defn operaciones-inseguras
  [cuantas ids frg db-spec]
  (j/with-db-connection [conn db-spec]
    (doseq [x (range 1 cuantas)]
      (try
        ((operacion-aleatoria conn ids frg))
        (catch Exception e (println "Error " (.getMessage e)))))))

(defn operaciones-seguras
  [cuantas ids frg db-spec]
  (j/with-db-connection  [connc db-spec]
    (j/with-db-transaction [conn connc]
      (doseq [x (range 1 cuantas)]
        (try
          ((operacion-aleatoria conn ids frg))
          (catch Exception e (println "Error " (.getMessage e))))))))


