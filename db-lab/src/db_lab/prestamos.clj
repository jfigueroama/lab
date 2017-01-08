(ns db-lab.prestamos
  (:require [clojure.java.jdbc :as j :refer
             [with-db-connection with-db-transaction]]
            [db-lab.utils :refer [q exec insert]]))


(defn prestamo-inseguro
  [db uid monto]
  (if-let [usu (first (q db "SELECT * from usuario WHERE id=:id" {:id uid}))]
    (do
      (exec db "UPDATE usuario SET deuda=deuda + :deuda" {:deuda monto})
      (insert db "INSERT INTO prestamo(usuario_id, monto) VALUES (:uid, :monto)"
                 {:uid uid :monto monto}))))

(defn pago-inseguro
  [db uid monto]
  (if-let [usu (first (q db "SELECT * from usuario WHERE id=:id" {:id uid}))]
    (if (<= monto (:deuda usu 0))
      (do
        (exec db "UPDATE usuario SET deuda=deuda - :deuda" {:deuda monto})
        (insert db "INSERT INTO pago(usuario_id, monto) VALUES (:uid, :monto)"
                   {:uid uid :monto monto}))
      (throw (Exception. "Pago incoherente.")))))

(defn pago-seguro
  [db uid monto]
  (try
    (exec db "START TRANSACTION")
    (let [id (pago-inseguro db uid monto)]
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
  [db uid]
  (let [monto (inc (rand-int 100000))
        cual (rand-nth '(:pago :prestamo))]
    (case cual
      :pago (partial prestamo-inseguro db uid monto)
      :prestamo (partial pago-inseguro db uid monto))))

(defn limpiar
  [cone]
  (with-db-connection [conn cone]
    (do
      (exec conn "DELETE FROM prestamo")
      (exec conn "DELETE FROM pago")
      (exec conn "UPDATE usuario SET deuda=0"))))

(defn operaciones-inseguras
  [cuantas db-spec]
  (j/with-db-connection [conn db-spec]
    (doseq [x (range 1 cuantas)]
      (try
        ((operacion-aleatoria conn 1))
        (catch Exception e (println "Error " (.getMessage e)))))))

(defn operaciones-seguras
  [cuantas db-spec]
  (j/with-db-transaction [conn db-spec]
    (doseq [x (range 1 cuantas)]
      (try
        ((operacion-aleatoria conn 1))
        (catch Exception e (println "Error " (.getMessage e)))))))


