(defproject plot-lab "0.1.0-SNAPSHOT"
  :description "Laboratorio "
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 ; client
                 [org.clojure/clojurescript "1.9.293"]
                 [devcards "0.2.2"]
                 [cljsjs/react "15.4.0-0"]
                 [cljsjs/react-dom "15.4.0-0"]
                 [reagent "0.6.0" 
                                    :exclusions
                                     [org.clojure/tools.reader
                                      cljsjs/react]]
                 [re-frame "0.9.0"  :exclusions [cljsjs/react]]
                 [binaryage/devtools "0.8.3"]
                 [quil "2.5.0"]
                 [cljs-ajax "0.5.8"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [keybind "2.0.0"]

                 ; server
                 [com.stuartsierra/component "0.3.1"]
                 [com.cognitect/transit-clj "0.8.295"]
                 [http-kit "2.2.0"]    ; web server
                 [cheshire "5.6.3"]    ; json
                 [ring/ring-core "1.5.0"] ; handlers
                 [javax.servlet/servlet-api "2.5"] ; manejo de datos POST
                 [compojure "1.5.1"]    ; web framework
                 [com.cognitect/transit-clj "0.8.288"]
                 [com.taoensso/sente "1.10.0"]
                 [hiccup "1.0.5"]

                 ; Utiles
                 [org.clojure/core.async "0.2.395"]
                 [funcool/cats "2.0.0"]
                 [funcool/lentes "1.1.0"]

                 ; DB
                 [com.layerware/hugsql "0.4.7"]
                 [mysql/mysql-connector-java "6.0.4"]

                 ; Otros
                 [hawk "0.2.10"]  ; File watcher
                 [clj-time "0.12.0"]
                 [lein-light-nrepl "0.3.3"]]



  ;:main server.core

  :plugins [[lein-figwheel "0.5.8"]
            [lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]] ]

  :source-paths ["src"] 
  :clean-targets ^{:protect false} ["target" "public/js/cards.js"
                                    "public/js/cards_out"]
  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.8.2"]
                   [figwheel-sidecar "0.5.8"]]
    :source-paths ["src"]
    :repl-options {; for nREPL dev you really need to limit output
                   :init (set! *print-length* 50)
                   :repl-options {:nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]}
                   }}}

  :cljsbuild
  {:builds
   [{:id           "devcards"
     :source-paths ["src"]
     :figwheel     {;:on-jsload "hcice.core/mount-root"
                    :devcards true
                    :load-warninged-code true
                    }
     :compiler     {:devcards true
                    :main                 cards.core
                    :output-to            "public/js/cards.js"
                    :output-dir           "public/js/cards_out"
                    :asset-path           "/public/js/cards_out"
                    :source-map-timestamp true}}

;    {:id           "dev"
;     :source-paths ["src"]
;     :figwheel     {;:on-jsload "hcice.core/mount-root"
;                    :load-warninged-code false
;                    }
;     :compiler     {:main                 hcice.core
;                    :output-to            "resources/public/js/hcice/app.js"
;                    :output-dir           "resources/public/js/hcice/out"
;
;                    :asset-path           "/js/hcice/out"
;                    :source-map-timestamp true}}

;    {:id           "prod"
;     :source-paths ["src"]
;     :compiler     {:main            hcice.core
;                    :output-to       "resources/public/js/hcice/app.js"
;                    :optimizations   :advanced
;                    :closure-defines {goog.DEBUG false}
;                    :pretty-print    false}}
;
    ]})
