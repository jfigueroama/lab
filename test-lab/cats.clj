(do
  (require '[cats.core :as m :refer
             [mempty mappend pure return bind mzero mplus guard
              fmap fapply alet mlet foldl foldr foldm
              bimap left-map right-map
              unless ap ap-> ap->> as-ap-> curry lift-m lift-a
              ->= ->>= <$> <*> <=< =<< >=> >> >>=
              as->=
              curry-lift-m 
              extract right-map 
              ;sequence mapseq forseq filter join  when
              ]] :reload)
  #_(require '[cats.builtin])
  (require '[cats.monad.maybe :as maybe :refer
             [just nothing cat-maybes from-maybe just? nothing? map-maybe 
              maybe->seq maybe? seq->maybe]] :reload)
  (require '[cats.monad.either :as either :refer
             [branch branch-left branch-right either? first-left first-right
              left left? right right? try-either
              ;invert 
              ]] :reload)
  (require '[cats.context :as ctx :refer [with-context]])
  (require '[clojure.java.jdbc :as j])
  )



(fmap inc (m/sequence [(just 1) (just 2)])) ; error
(fmap inc [1 2 nil 3])  ; error

(fmap inc (just 0)) ; just 1
(fmap inc (right 0)) ; right 1
(fmap inc (left "error")) ; "error"
(fmap inc nil)  ; nil
(fmap inc (nothing))  ; nothing
(inc nil)   ; Exception

(fapply (just inc) (just 0))  ; just 1
(fapply (right inc) (just 0)) ; just 0

(def ma {:a (just 1)})
(def mb {:a (nothing)})

(as-> mb ma
    (:a ma)
    (fmap inc ma))  ; nothing

(ap + (just 1) (nothing) (just 2))  ; nothing
(ap / (just 10) (just 2)) ; just 5
(ap / (right 10) (left "error!")) ; error!
(try-either (/ 10 0))  ; left: divide by zero
(try-either (/ 10 2))  ; right
(ap / (right 10) (right 2))
(ap / (right 10) (right 0))


(fapply (just inc) (just 2))
(fapply (just inc) (right 3))
(fmap inc (right 3))

(defn minc [v] (return (inc v)))
(defn mincn [v] (nothing))
(->= (just 0) minc minc  mincn minc)  ; nothing
(->= (just 0) minc minc  minc minc)  ; just 4

(defn einc [v] (return (inc v)))
(defn eincl [v] (left (str "Valor de '" v "' invalido")))

(>>= (just 1) minc minc minc) ; just 4

(->= (just 0)      minc     mincn     minc     minc) ; nothing
(->= (right 0) einc einc einc) ; right 3
(->= (left "Error a proposito") einc einc einc) ; Error a proposito
(->= (right 0) einc einc einc eincl einc) ; left Valor de '3' invalido.

(m/filter (partial < 5) [1 2 3 4 5 6 7 8 9 ]) ; [6 7 8 9]
(m/filter (partial < 5) (just 2)) ; nothing



; Repaso:
; Functor: sabe como aplicarle una funcion normal pura a un valor functor.
; Applicative: Sabe como aplicarle una función aplicativa (recibe un valor
;   normal, devuelve un valor normal, esta encapsulada) a un valor aplicativo
;   (encapsulado).
; Monad: Sabe como encadenar una funcion monádica (recibea un valor nomral,
;   devuelve un valor monádico) no encapsulada, y un valor monádico
;   (encapsulado)


(alet [a (right 1)
       ;b (left "error")
       b (right 2)
       c (right 3)
       ;c 3 ; Si hay un valor encapsulado, el valor devuelto es el encapsulado.
            ; Creo que es un error grave y no salta nada. Hay que ver que pedo.
       ]
      (+ a b c))

;;;;;
; 20170922
(comp just inc)
((comp just inc) 1)
((comp #(* 2 %) inc) 0)

(just 1)

(defn minc
  [x]
  (just (inc x)))

(defn msum
  [& xs]
  (just (apply + xs)))
(msum 1 2 3 4)


(minc 2)
(defn mincn
  [x]
  (nothing))

(->= (just 1)
     minc
     minc)  ; => just 3

(as->= (just 1) x
       (mincn x)
       (minc x)
       (minc x))  ; => nothing


(fapply (just inc) (just 10))

(as->= (just 1) x
       (fapply (just inc) (just x))
       (minc x)
       (minc x))  ; => just 4

(as->= (nothing) x
       (fapply (just inc) (just x))
       (minc x))

(as->= (just 1) x
       (minc x))

(ap + (just 1) (just 2))
(ap + (just 1) (nothing) (just 2))


(fapply (just +) (just 1) (just 2) (just 3))
(fapply (nothing) (just 1))
(fapply (just +) (just 1))
(fapply (just +) (nothing))

(ap-> (just 1)
      inc
      inc)

(defn div0
  [x]
  (/ x 0))

(ap-> (just 1)
      inc
      div0
      inc)

(ap-> (nothing)
      inc
      inc)


(defn mget
  [hs k]
  (if (nothing? (get hs k))
    (nothing)
    (just (get hs k))))

(defn eget
  [hs k]
  (if (nothing? (get hs k))
    (left k)
    (right (get hs k))))

(defn minc
  [x]
  (just (inc x)))

(->= (just {:a 1 :b 2})
     (mget :c)  ; nothing
     minc)      ; => nothing

(->= (just {:a 1 :b 2})
     (mget :b)  ; just 2
     minc)    ; just 3
