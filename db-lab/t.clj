(require '[com.hypirion.clj-xchart :as c])

(require '[db-lab.utils :refer [q exec insert]])
(require '[db-lab.prestamos :refer
           [operaciones-seguras operaciones-inseguras limpiar fecha-random-gen
            operacion-aleatoria pago-inseguro prestamo-inseguro]] :reload)


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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; UTILS
(def fecha-random
  (partial fecha-random-gen {:year (range 2000 2017) :month (range 1 13) :day (range 1 29)
                             :hour (range 7 17) :minute (range 1 60) :second (range 1 60)}))


;;; ---------------------------------

(prestamo-inseguro epdb  1 500 (fecha-random))

(q epdb "SELECT * FROM comprobador")

(doseq [x (range 0 10)]
  (future (operaciones-inseguras 100 (range 1 11) fecha-random epdb)))

(doseq [x (range 1 100)]
  (future (operaciones-seguras 100 (range 1 11) fecha-random epdb)))

(limpiar epdb)

;; --------------------------
(limpiar pdb)


(doseq [x (range 1 10)]
  (future (operaciones-seguras 1000 (range 1 11) fecha-random pdb)))
;; TODO ver y descargar articulos sobre connection pooling en java

;; alter table prestamo add column (realizado_el datetime default null);
;; Ver si h2 vale para base de datos embedida para el sistema del
;; seminario


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def dbc {:classname "org.hsqldb.jdbc.JDBCDriver"
          :subprotocol "hsqldb"
          :subname (str "./db/db.hsqldb")
          :user "SA"
          :password ""})

(def fecha-random
  (partial fecha-random-gen {:year (range 2000 2017) :month (range 1 13) :day (range 1 29)
                             :hour (range 7 17) :minute (range 1 60) :second (range 1 60)}))

(q dbc "select * from usuario")
(insert dbc "insert into usuario(id, nombre, deuda) values (:id, :n, :d)"
        {:id 10 :n "jorge" :d 0})

(exec dbc "CREATE VIEW prestado AS SELECT p.usuario_id AS usuario_id ,coalesce(sum(p.monto), 0) AS prestado FROM prestamo AS p GROUP BY p.usuario_id")
(q dbc "select * from prestado")
(exec dbc "CREATE VIEW pagado AS SELECT p.usuario_id AS usuario_id ,coalesce(sum(p.monto), 0) AS pagado FROM pago AS p GROUP BY p.usuario_id")
(q dbc "select * from pagado")
(exec dbc "CREATE VIEW comprobador AS SELECT u.id AS id,u.nombre AS usuario,u.deuda AS deuda,(coalesce(spr.prestado,0) - coalesce(spa.pagado,0)) AS deuda_real from ((usuario AS u left join prestado AS spr on ((spr.usuario_id = u.id))) left join pagado AS spa on ((spa.usuario_id = u.id)))")


(pprint (q dbc "select * from comprobador"))

(doseq [x (range 1 20)]
  (future (operaciones-seguras 1000 (range 1 11) fecha-random dbc)))

(doseq [x (range 0 10)]
  (future (operaciones-inseguras 1000 (range 1 11) fecha-random dbc)))

(limpiar dbc)

(exec dbc "shutdown")





