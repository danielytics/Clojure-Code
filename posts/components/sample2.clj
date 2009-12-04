(run-components
  (make-component "Comp1"
                  {:init (fn [v s]
                           (event :print nil)       
                           (event :set-msg "Ho")
                           (event :print nil)
                           {:msg "Hi"})
                   :print (fn [v s]
                            (println (:msg s)))
                   :set-msg (fn [v s]
                              {:msg v})})

  (make-component "Comp2"
                  {:init (fn [v s]
                           {:msg "Hello"})
                   :print (fn [v s]
                            (println (:msg s)))}))

