; ((:chsk-send! comm) :sente/all-users-without-uid [:evento/datos {:a 1 :b 2}])
;;;
;;; A little explanation of some issues that someone could encounter
;;; working with Stuart Sierra's component:
;;; I needed to include all component namespaces to be able to start the
;;; system. I don't know why and I will find an answer, but for now, you
;;; need to include all component namespaces to start/stop the system
;;; from a nrepl. This issue doesn't happen on clojure started from
;;; java and including extra classpath. It just happen in the nrepl.
;;;
;;; I figured out the problem when I included some components and those
;;; component started, but not all of them. Just started the componets
;;; whose namespaces was included with require or use.
;;;
;;; @20161026 Jose Figueroa Martinez


(require '[server.utils.watch :refer :all] :reload)

(def ^:macro λ #'fn)
(require '[clojure.string :as st] :reload)
(require '[clojure.pprint :refer [pprint ]] :reload)
(require '[clojure.java.jdbc :as j] :reload)
(require '[com.stuartsierra.component :as component] :reload)
(require '[clojure.java.jdbc :as j] :reload)

;(require '[server.utils :as utils])

(def config
   {:mode :cards  ; :prod
    :db {:subname (str "//localhost:3306/ca?"
                       "useUnicode=yes"
                       "&characterEncoding=UTF-8"
                       "&serverTimezone=UTC")
         :user "root"
         :db (str "ca?"
                  "useUnicode=yes"
                  "&characterEncoding=UTF-8"
                  "&serverTimezone=UTC")
         :password ""
         :useUnicode "yes"
         :characterEncoding "UTF-8"}
    :webapp {:port 9002
             :mode :dev
             :base-url "http://localhost:9002"
             :php-base-url "http://localhoca/shp/"}})


(reload (use 'server.core) "./src/server/core.clj")


(def system nil)
(defn reload-system
  []
  (do
    (alter-var-root #'system component/stop)
    (alter-var-root #'system component/start)))

(reload (require '[server.utils :as utils])
        "./src/server/utils.clj"
        (reload-system))

(reload (require '[server.components.database :as database])
        "./src/server/components/database.clj"
        (reload-system))


(reload (require '[server.components.communicator :as comm])
        "./src/server/components/communicator.clj"
        (reload-system))

(reload (require '[server.components.handler :as handler])
        "./src/server/components/handler.clj"
        (reload-system))

(reload (require '[server.components.webserver :as webserver])
        "./src/server/components/webserver.clj"
        (reload-system))

(reload (require '[server.components.app :as app])
        "./src/server/components/app.clj"
        (reload-system))

(reload (require '[server.handlers :as handlers])
        "./src/server/handlers.clj"

        (reload-system))

(def system (app config))
(alter-var-root #'system component/start)
(def dbc (:db system))
(def comm (:comm system))
