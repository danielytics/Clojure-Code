(ns test
  (:import org.lwjgl.input.Keyboard)
  (:use sushi
        [clojure.contrib.seq-utils :only (includes?)]))

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

