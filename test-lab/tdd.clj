(require '[cljs.spec.alpha :as s])
(require '[cljs.spec.gen.alpha :as gen])
(require '[cljs.spec.test.alpha :as st])



(require '[clojure.spec.alpha :as s])
(require '[clojure.spec.gen.alpha :as gen])
(require '[clojure.spec.test.alpha :as st])

(require '[clojure.test.check :as check])

(require '[cats.core :as cats])
(require '[cats.monad.maybe :as maybe])



(defn mget
  "Es como el get pero monadico."
  [hm k]
  (let [v (get hm k ::mget-default)]
    (if (not= v ::mget-default)
      (maybe/just v)
      (maybe/nothing))))

(mget {:a 1} :a)
(mget {:a 1} :b)


(s/def ::maybe-nothing
  (s/spec maybe/nothing?
          :gen (fn maybe-nothing-gen [] (->> (s/gen nil?) (gen/fmap (fn [_](maybe/nothing)))))))

(s/exercise ::maybe-nothing)

(s/def ::maybe-just
  (s/spec maybe/just?
          :gen (fn maybe-just-gen [] (->> (s/gen some?) (gen/fmap #(maybe/just %))))))

(s/exercise ::maybe-just)

(s/def ::maybe
  (s/spec maybe/maybe?
          :gen (fn maybe-gen [] (gen/one-of [(s/gen ::maybe-nothing) (s/gen ::maybe-just)]))))

(s/exercise ::maybe)


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





