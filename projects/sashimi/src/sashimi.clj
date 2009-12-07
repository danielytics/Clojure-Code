(ns sashimi)

(defn gen-fn [as b]
  (let [as       (vec (map symbol as))
        fn-value (eval `(fn ~as ~b))]
    (println `(fn ~as ~b))
    fn-value))

(defn gen-code [nesting max-nesting args]
  (if (= nesting max-nesting)
    (rand-int 100)
    (let [ops      '(+ - * /)
          params   '(expr const arg)
          get-from #(nth % (rand-int (count %)))]
      (cons (get-from ops)
            (map #(cond (= % 'expr)  (gen-code (inc nesting) max-nesting args)
                        (= % 'const) (rand-int 100)
                        (= % 'arg)   (get-from args))
                 (take (+ 2 (rand-int 3))
                       (iterate (fn [_] (get-from params))
                                (get-from params))))))))

(defn get-func [num-args max-depth]
  (let [args (take num-args '(a b c d e f g h i j))]
    (gen-fn args (gen-code 1 max-depth args))))

