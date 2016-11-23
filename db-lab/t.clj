(require '[hugsql.core :refer [db-run]])
(require '[com.hypirion.clj-xchart :as c])

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
         

(db-run db "select * from profesor")

(defn q
  ([db sql params]
   (db-run db sql params))
  ([db sql]
   (q db sql {})))

(defn exec
  ([db sql params]
   (db-run  db sql params :execute :affected))
  ([db sql]
   (exec db sql {})))

(defn insert
  ([db sql params]
   (db-run  db sql params :insert :returning-execute))
  ([db sql]
   (insert db sql {})))



(def profesxi
  (db-run db "
          SELECT n.instituto, count(n.pid) AS profes FROM
             (select i.id, i.nombre AS instituto, p.id AS pid
          FROM instituto AS i LEFT JOIN profesor AS p ON i.id=p.instituto_id) AS n
             GROUP BY n.instituto;"))

(c/view
    (c/pie-chart (zipmap (map :instituto profesxi) (map :profes profesxi))
                 {:title "Profes x instituto"}))


