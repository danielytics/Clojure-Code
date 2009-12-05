(ns lunchbox
  (:use clojure.set
        emitter)
  (:import [java.util ArrayDeque]))


; Register list
(defn make-reglist [live dead]
  (list live dead))

(defn live [reglist]
  (first reglist))

(defn dead [reglist]
  (second reglist))

(defn grab [n reglist]
  "Grab n registers from the reglist, pop 'dirty' if needed"
  (let [l (live reglist)
        d (dead reglist)
        c (count l)]
    (if (>= c n)
      (list (take n l) (make-reglist (drop n l) d) '())
      (let [diff (- n c)
            new (take diff d)]
        (list (concat l new) (make-reglist '() (drop diff d)) new)))))

(defn build [reglist r-live r-dead]
  (make-reglist (concat (live reglist) r-live)
                (concat (dead reglist) r-dead)))

(defn with-regs [n oper reglist]
  (let [[regs new-reglist dirty] (grab n reglist)]
    (list 
      (concat
        (map #(emit 'pop %) dirty)
        (apply emit (concat (list oper) regs)))
      (build new-reglist
             (list (first regs))
             (rest regs)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; PROCESS INSTRUCTIONS 
;

(defmulti instruction (fn [a & b] a))
(defmethod instruction :binop [_ op reglist]
  (with-regs 2 op reglist))


(defn flatten [ops indent operands]
  (reduce (fn [a b]
            (str a "\n"
              (if (= (:opcode b) :raw)
                (:operand b)
                (format (str "%" indent "s%-" operands "s %s")
                        " " (:opcode b)
                        (reduce (fn [a b] (str a ", " b))
                                (:operands b))))))
          (concat '("") ops)))

