(require 'cljs.build.api)

(cljs.build.api/build
  "src"
  {:main 'metae.core
   :source-map-timestamp true
   :optimizations :whitespace
   :output-dir "out"
   :source-map "out/main.js.map"
   :output-to "out/main.js"})
