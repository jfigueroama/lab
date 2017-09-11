(defproject web-lab "0.1.0-SNAPSHOT"
  :description "Laboratorio para cosas de web."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha20"]
             
                 [jfigueroama/watch "0.1.0"]
                 ; Client
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.7.0" :exclusions
                                     [org.clojure/tools.reader
                                      cljsjs/react
                                      ;cljsjs/react-dom
                                      ]]
                 [binaryage/devtools "0.9.4"]
                 [cc.qbits/jet "0.7.11"]  ; jetty9
                 [re-frame "0.10.1" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [cljs-ajax "0.7.2"]
                 [cljs-react-material-ui "0.2.48"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [keybind "2.1.0"]

                 ; Server
                 [http-kit "2.2.0"]    ; web server
                 [cheshire "5.8.0"]    ; json
                 [ring/ring-core "1.6.2"] ; handlers
                 [javax.servlet/servlet-api "2.5"] ; manejo de datos POST
                 [compojure "1.6.0"]    ; web framework
                 [com.cognitect/transit-clj "0.8.300"]
                 [hiccup "1.0.5"]

                 ; DB
                 [com.layerware/hugsql "0.4.7"]
                 [mysql/mysql-connector-java "6.0.6"]
                 [org.xerial/sqlite-jdbc "3.19.3"]
                 [com.h2database/h2 "1.4.196"]
                 [org.postgresql/postgresql "42.1.4"]

                 [org.clojure/core.async "0.3.443"]
                 [funcool/cats "2.1.0"]
 
                 ; Otros
                 [clj-time "0.14.0"]
                 [org.clojure/test.check "0.9.0"]
                 [lein-light-nrepl "0.3.3"]]

  ;:main server.core

  :plugins [;[lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]
            [lein-ancient "0.6.10"]]

  :min-lein-version "2.5.3"

  :source-paths ["src"]

  :clean-targets
  ^{:protect false}  ["target"]

  :profiles
  {:dev
   {:dependencies [[devcards "0.2.3" :exclusions [cljsjs/react cljsjs/react-dom]]]

    :plugins      [[lein-figwheel "0.5.13"]]}

   :devcards {:dependencies [[devcards "0.2.3" :exclusions [cljsjs/react cljsjs/react-dom]]]

              :plugins      [[lein-figwheel "0.5.13"]]}}



  :repl-options {:nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]})
