(use '[uncomplicate.clojurecl core info])

(map info (platforms))
(pprint (map info (devices (first (platforms)))))


(ns uncomplicate.clojurecl.examples.openclinaction.ch04
  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [chan <!!]]
            [uncomplicate.commons.core :refer [with-release]]
            [uncomplicate.clojurecl
             [core :refer :all]
             [info :refer [info endian-little]]]
            [vertigo
             [bytes :refer [direct-buffer byte-seq]]
             [structs :refer [wrap-byte-seq int8]]]))



(let [notifications (chan)
      follow (register notifications)]

(with-release [dev (first (devices (first (platforms))))
                 ctx (context [dev])
                 cqueue (command-queue ctx dev)]

    (let [host-msg (direct-buffer 16)
          work-sizes (work-size [1])
          program-source
          (slurp (io/resource "examples/openclinaction/ch04/hello-kernel.cl"))]
      (with-release [cl-msg (cl-buffer ctx 16 :write-only)
                     prog (build-program! (program-with-source ctx [program-source]))
                     hello-kernel (kernel prog "hello_kernel")
                     read-complete (event)]

        (set-args! hello-kernel cl-msg) => hello-kernel
        (enq-nd! cqueue hello-kernel work-sizes) => cqueue
        (enq-read! cqueue cl-msg host-msg read-complete) => cqueue
        (follow read-complete host-msg) => notifications
        (apply str (map char
                        (wrap-byte-seq int8 (byte-seq (:data (<!! notifications))))))
