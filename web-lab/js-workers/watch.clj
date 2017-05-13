(require 'cljs.build.api)

(cljs.build.api/watch
  "src"
  {:main 'metae.core
   :output-to "out/main.js"})
