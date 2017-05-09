(defproject web-lab "0.1.0-SNAPSHOT"
  :description "Laboratorio para cosas de web."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 
                 ; Client
                 [org.clojure/clojurescript "1.9.293"]
                 [reagent "0.6.0" :exclusions
                                     [org.clojure/tools.reader
                                      cljsjs/react
                                      ;cljsjs/react-dom
                                      ]]
                 [binaryage/devtools "0.8.3"]
                 [cc.qbits/jet "0.7.11"]  ; jetty9
                 [re-frame "0.9.0" :exclusions [cljsjs/react]]
                 [cljs-ajax "0.5.8"]
                 [cljs-react-material-ui "0.2.30"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [keybind "2.0.0"]

                 ; Server
                 [http-kit "2.2.0"]    ; web server
                 [cheshire "5.6.3"]    ; json
                 [ring/ring-core "1.5.0"] ; handlers
                 [javax.servlet/servlet-api "2.5"] ; manejo de datos POST
                 [compojure "1.5.1"]    ; web framework
                 [com.cognitect/transit-clj "0.8.288"]
                 [hiccup "1.0.5"]

                 ; DB
                 [com.layerware/hugsql "0.4.7"]
                 [mysql/mysql-connector-java "6.0.4"]

                 ; Otros
                 [hawk "0.2.10"]  ; File watcher
                 [clj-time "0.12.0"]
                 [lein-light-nrepl "0.3.3"]]

  :main server.core

  :plugins [;[lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]
            [lein-ancient "0.6.10"]]

  :min-lein-version "2.5.3"

  :source-paths ["src"]

  :clean-targets
  ^{:protect false}  ["target"]

  :profiles
  {:dev
   {:dependencies [[devcards "0.2.2" :exclusions [cljsjs/react cljsjs/react-dom]]]

    :plugins      [[lein-figwheel "0.5.4-7"]]}

   :devcards {:dependencies [[devcards "0.2.2" :exclusions [cljsjs/react cljsjs/react-dom]]]

              :plugins      [[lein-figwheel "0.5.4-7"]]}}



  :repl-options {:nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]})
