(program
  ; Define a component.
  (component "Comp1"
    ; Event handler for :init events
    (on :init [v s]
        ; Trigger some events
        (event :print nil)
        (event :set-msg "Ho")
        (event :print nil)
        ; Set this components state
        {:msg "Hi"})

    (on :print [v s]
        (println (:msg s)))

    ; An event handler which updates the components state
    (on :set-msg [v s]
        {:msg v}))

  (component "Comp2"
    (on :init [v s]
        {:msg "Hello"})

    (on :print [v s]
        (println (:msg s)))))

