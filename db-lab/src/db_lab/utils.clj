(ns db-lab.utils
  (:require [hugsql.core :refer [db-run]]))

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



