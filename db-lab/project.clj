(defproject db-lab "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.hypirion/clj-xchart "0.2.0"]
                 [cljsjs/vega "2.6.0-0"]
                 [cljsjs/d3 "4.3.0-2"]
                 [gorilla-renderable "2.0.0"]
                 [aysylu/loom "0.6.0"]
                 [loom-gorilla "0.1.0"]

                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [com.layerware/hugsql "0.4.7"]
                 [org.hsqldb/hsqldb "2.3.4"]
                 [org.xerial/sqlite-jdbc "3.16.1"]

                 ]
  :plugins [[lein-gorilla "0.4.0"]])
