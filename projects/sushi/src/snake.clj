(ns snake
  (:import org.lwjgl.input.Keyboard)
  (:use sushi))

(defstruct state-object :running :angle :inc-rate)
(defn make-state []
  (struct state-object true 0.0 0.1))

(defn logic-func [state]
  {:angle (mod (+ (:angle state)
                  (:inc-rate state)) 360)})

(run-game "Test Game"
          make-state
          logic-func
          render-func
          [[Keyboard/KEY_ESCAPE :running (fn [s] false)]
           [Keyboard/KEY_SPACE  :inc-rate (fn [s]
                                            (if (< 1.0 (:inc-rate s))
                                              0.1
                                              2.0))]])
(comment
Sushi to implement events, a rendering system and a game DSL.
DSL for defining games should look something like this:

(game
  (name "Snake Game")
  (input
    ; Map keyboard input to events
    (on-keypress 'KEY_ESCAPE :terminate   nil   )
    (on-keypress 'KEY_SPACE  :toggle-rate nil   )
    (on-keydown  'KEY_LEFT   :move        :left )
    (on-keydown  'KEY_RIGHT  :move        :right)
    (on-keydown  'KEY_UP     :move        :up   )
    (on-keydown  'KEY_DOWN   :move        :down ))

  ; Setup global event handlers
  (global-handlers
    (on :terminate [v s]
        (return :running false))

    (on :toggle-rate [v s]
        (return :inc-rate
                (if (< 1.0 (:inc-rate s))
                  0.1
                  2.0))))

  ; Define game entities
  (entities
    (entity "Snake"
      (on :init [v s]
        (return :position [0 0])))

    (entity "Apple"
      (on :init [v s]
        (return :position [0 0])))))
)
