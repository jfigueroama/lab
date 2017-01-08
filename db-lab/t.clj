(require '[com.hypirion.clj-xchart :as c])

(require '[db-lab.utils :refer [q exec insert]])
(require '[db-lab.prestamos :refer
           [operaciones-seguras operaciones-inseguras
            operacion-aleatoria pago-inseguro prestamo-inseguro]])


#_(require '[gorilla-plot.core :as gp :refer [plot histogram bar-chart list-plot]])
#_(require '[gorilla-repl.table :as gt :refer [table-view]])

(def db {:subname (str "//localhost:3306/ca?"
                       "useUnicode=yes"
                       "&characterEncoding=UTF-8"
                       "&serverTimezone=UTC")
         :user "root"
         :password ""
         :useUnicode "yes"
         :characterEncoding "UTF-8"
         :classname "com.mysql.cj.jdbc.Driver"
         :subprotocol "mysql"})

(def pdb {:subname (str "//localhost:3306/prestamos?"
                       "useUnicode=yes"
                       "&characterEncoding=UTF-8"
                       "&serverTimezone=UTC")
         :user "root"
         :password ""
         :classname "com.mysql.cj.jdbc.Driver"
         :subprotocol "mysql"})

(def epdb {:subname (str "//10.10.1.148:3306/prestamo?"
                       "useUnicode=yes"
                       "&characterEncoding=UTF-8"
                       "&serverTimezone=UTC")
         :user "puser"
         :password "secreto"
         :classname "com.mysql.cj.jdbc.Driver"
         :subprotocol "mysql"})



;(db-run db "select * from profesor")

(exec db "CREATE TEMPORARY TABLE pjose AS (SELECT * FROM profesor WHERE nombres LIKE :nombres ESCAPE \"!\")" {:nombres "%os%"})

(exec db "DROP VIEW pjose")


(db-run db "CREATE TEMPORARY TABLE pjose AS (SELECT * FROM profesor WHERE nombres LIKE :nombres ESCAPE \"!\"); SELECT * FROM pjose " {:nombres "%os%"})


(def profesxi
  (db-run db "
          SELECT n.instituto, count(n.pid) AS profes FROM
             (select i.id, i.nombre AS instituto, p.id AS pid
          FROM instituto AS i LEFT JOIN profesor AS p ON i.id=p.instituto_id) AS n
             GROUP BY n.instituto;"))

(c/view
    (c/pie-chart (zipmap (map :instituto profesxi) (map :profes profesxi))
                 {:title "Profes x instituto"}))


#_(let [profes (db-run db "select * from profesor")]
      rows (map (fn [p]
                  (map (fn [[k v]]v)
                       p)) profes)
      columns (mapv (fn [[k v]] k) (first profes)))
  (table-view rows :columns columns)

#_(let [r (db-run db "select i.id, i.nombre AS instituto, count(p.id) AS profesores FROM instituto AS i LEFT JOIN profesor AS p ON i.id=p.instituto_id GROUP BY i.id")])
  (bar-chart (map :instituto r) (map :profesores r) :plot-size 2000)

(defmacro qs
  [db & args]
  (let [se (str args)]
  `(q db ~se)))

(macroexpand-1 '(qs SELECT * FROM casa LIKE "\"mundo"))

(qs db SELECT * FROM  profesor WHERE apellidos LIKE "%Figueroa%")

;; ----------------------------------------------------------
; PRESTAMOS

;;; ---------------------------------
(doseq [x (range 0 10)]
  (future (operaciones-inseguras 3000 pdb)))

(doseq [x (range 0 10)]
  (future (operaciones-seguras 3000 pdb)))



(doseq [x (range 1 10000)]
  (future
    (try
      (operacion-segura pdb (operacion-aleatoria pdb 1))
      (catch Exception e (prn (.getMessage e))))))

(prestar-inseguro pdb 1 100)
(pagar-inseguro pdb 1 100)
(pagar-seguro pdb 1 100)
