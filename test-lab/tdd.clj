(require '[cljs.spec.alpha :as s])
(require '[cljs.spec.gen.alpha :as gen])
(require '[cljs.spec.test.alpha :as st])



(require '[clojure.spec.alpha :as s])
(require '[clojure.spec.gen.alpha :as gen])
(require '[clojure.spec.test.alpha :as st])

(require '[clojure.test.check :as check])

(require '[cats.core :as cats])
(require '[cats.monad.maybe :as maybe])
(require '[cats.monad.either :refer [right left try-either]])
(require '[cats.monad.exception :as exc])

(defn efy
  "Decorates a function tfn in order to return a either value.
  The optional iserrfn tells if a value is an error or left value.
  The default error value is nil.

  The returned value inside a left is like {:fn tfn :args args}."
  ([tfn]
   (efy (fn efy-def-iserrfn
          [res]
          (nil? res))
        tfn))
  ([iserrfn tfn]
   (fn [& args]
     (let [v (apply tfn args)]
       (if (iserrfn v)
         (left {:fn tfn :args args})
         (right v))))))

(defn sefy
  "Decorates a function tfn in order to return a either value.
  The optional iserrfn tells if a value is an error or left value.
  The default error value is nil.
  This function also catch exceptions (safe eitherfy).

  The returned value inside a left is like {:fn tfn :args args}."
  ([tfn]
   (sefy (fn sefy-def-iserrfn
           [res]
           (nil? res))
         tfn))
  ([iserrfn tfn]
  (fn [& args]
    (try
      (let [v (apply tfn args)]
        (if (iserrfn v)
          (left {:fn tfn :args args})
          (right v)))
      (catch Exception e
        (left {:fn tfn :args args :ex e}))))))


(defn exy
  "Decorates a function tfn in order to return a Exception Monad value.
  The optional iserrfn tells if a value is an error or left value.
  The default error value is nil.
  This function also catch exceptions (safe eitherfy).

  The returned value inside a left is like {:fn tfn :args args}."
  ([tfn]
   (sefy (fn sefy-def-iserrfn
           [res]
           (nil? res))
         tfn))
  ([iserrfn tfn]
  (fn [& args]
    (exc/try-on (apply tfn args)))))

; TODO hacer un macro para definir conversiones a sefy pero respetando las
; documentaciones!.

(defmacro defsefy
  "Defines a function fname that"
  [fname] nil)


; Common functions decorated to return either values.
(def efirst (sefy first))
(def eget (sefy get))
(def eget-in (sefy get-in))


(efirst 234)
(efirst [1 2])
(efirst nil)
(let [a {:a 1 :b {:b1 "hola"}}]
  (eget-in a [:b :b1]))



(defn mget
  "Es como el get pero monadico."
  [hm k]
  (let [v (get hm k ::mget-default)]
    (if (not= v ::mget-default)
      (maybe/just v)
      (maybe/nothing))))

(s/def ::myint (s/spec int?))
(s/exercise ::myint)

(mget {:a 1} :a)
(mget {:a 1} :b)


(s/def ::maybe-nothing
  (s/spec maybe/nothing?
          :gen (fn maybe-nothing-gen [] (->> (s/gen nil?) (gen/fmap (fn [_](maybe/nothing)))))))

(s/exercise ::maybe-nothing)

(s/def ::maybe-just
  (s/spec maybe/just?
          :gen (fn maybe-just-gen [] (->> (s/gen some?) (gen/fmap #(maybe/just %))))))

(pprint (s/exercise ::maybe-just))

(s/def ::maybe
  (s/spec maybe/maybe?
          :gen (fn maybe-gen [] (gen/one-of [(s/gen ::maybe-nothing) (s/gen ::maybe-just)]))))

(pprint (s/exercise ::maybe))


(s/fdef mget
  :args (s/cat :hm map? :k keyword?)
  :fn (fn mget-fn [{{hm :hm k :k} :args ret :ret}]
        (if (= (get hm k ::mget-nothing) ::mget-nothing)
          (maybe/nothing? ret)
          (maybe/just? ret)))
  :ret ::maybe)


(st/summarize-results (st/check `mget {:clojure.spec.test.check/opts {:num-tests 50}}))
(st/check `mget  {:clojure.spec.test.check/opts {:num-tests 100}})
(st/instrument `mget)
(st/deinstrument `mget)


(defn abs
  [x]
  (if (or (Double/isNaN x)
          (Double/isInfinite x))
    x
    (if (> x 0)
      x
      (* -1 x))))

(s/fdef abs
  :args (s/cat :x number?)
  :ret number?
  :fn (fn abs-fdef-fn
        [{{x :x} :args ret :ret}]
        (or (= x ret)
            (= x (* -1 ret)))))


(st/summarize-results (st/check `abs {:clojure.spec.test.check/opts {:num-tests 10000}}))
(st/check `abs {:clojure.spec.test.check/opts {:num-tests 1000}})
(s/exercise-fn `abs)
(st/check-fn abs)



(s/exercise (:args (s/get-spec `abs)))
(s/exercise (:ret (s/get-spec `abs)))





