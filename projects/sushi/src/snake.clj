(ns snake
  (:use sushi.sushi
        [clojure.contrib.seq-utils :only (includes?)]))

; Snake game implemented in proposed DSL:

; Menu constants
(def MENU_PLAY 0)
(def MENU_QUIT 1)

; Direction constants
(def DIR_UP    [ 0  1])
(def DIR_DOWN  [ 0 -1])
(def DIR_LEFT  [-1  0])
(def DIR_RIGHT [ 1  0])

; Game world constants
(def WIDTH       75)
(def HEIGHT      50)
(def POINT-SIZE  10)
(def TURN-MILLIS 75)

; Data structures and constructors
(defstruct title-s :selection)
(defn make-title [] (struct title-s 0))

(defstruct apple-s :location :color)
(defn make-apple []
  (struct apple-s [(rand-int width)
                   (rand-int height)] [210 50 90]))

(defstruct snake-s :body :direction :color)
(defn make-snake []
  (struct snake-s (list [1 1]) DIR_RIGHT [15 160 70]))

; Convenience functions
(defn add-points [& pts]
  (vec (apply map + pts)))

(defn point-to-screen-rect [pt]
  (map #(* POINT-SIZE %)
       [(pt 0) (pt 1) 1 1]))

; Game state update functions
(defn move [{:keys [body direction]} & grow]
  {:body (cons (add-points (first body) direction)
               (if grow body (butlast body)))})

(defn eats? [{[head] :body} {apple :location}]
  (= head apple))

; Define and run the game
(game "Snake Game"
  ; Set the initial state
  (start-state :title)

  ; Define game states
  (states
    ; Title menu
    (state :title
      ; Define input map for this state
      (map-input
        (on-keypress 'KEY_UP     :menu   :up  )
        (on-keypress 'KEY_DOWN   :menu   :down)
        (on-keypress 'KEY_RETURN :select nil  ))

      ; Define entities for the :title state
      (entities
        (entity :Menu
          (on :init [v s]
            (make-title))

          ; Menu selection has been changed
          (on :menu [v s]
            {:selection
              (let [sel (+ (:selection s)
                           (cond (= v :up  ) -1
                                 (= v :down)  1
                                 :else        0))]
                (cond (< sel MENU_PLAY) 0
                      (> sel MENU_QUIT) MENU_QUIT
                      :else             sel))})

          ; Menu selection has been activated
          (on :select [v s]
            (let [sel (:selection s)]
              (cond (= sel MENU_PLAY) (state :game)
                    (= sel MENU_QUIT) (state :terminate))))
          
          ; Render the menu
          (render [s]
            nil))))

    ; Game mode
    (state :game
      ; Define input map for this state
      (map-input
        (on-keypress 'KEY_ESCAPE :state :title)
        (on-keydown  'KEY_LEFT   :turn  :left )
        (on-keydown  'KEY_RIGHT  :turn  :right)
        (on-keydown  'KEY_UP     :turn  :up   )
        (on-keydown  'KEY_DOWN   :turn  :down ))

      ; Define entities for the :game state
      (entities
        ; Player controlled snake entity
        (entity :Snake
          (on :init [v s]
            (make-snake))

          (on :turn [v s]
            {:direction ({:up    DIR_UP
                          :down  DIR_DOWN
                          :left  DIR_LEFT
                          :right DIR_RIGHT} v)})
          (on :eat [v s]
              {:eat true})

          (on :move [v s]
            (let [body (move s (:eat s))]
              (if (includes? body (first (:body s)))
                {:game-over true}
                (conj {:eat false} body))))

          (on :update [v s]
            (when-not (:game-over s)
              (event :try-eat nil)
              (event :move nil)))

          ; Define how a snake is to be drawn
          (render [s]
            (let [color (:color s)]
              (map (fn [b]
                     [:quad (point-to-screen-rect b) color nil])
                   (:body s)))))

        ; An apple entity, for the snake to eat
        (entity :Apple
          (on :init [v s]
            (make-apple))

          (on :try-eat [v s]
              (when (= (:location s) v)
                (event :eat nil)
                (event :new-apple)))

          (on :new-apple [v s]
            (make-apple))
          
          ; Define how an apple is to be drawn
          (render [s]
            [[:quad
              (point-to-screen-rect (:location s))
              (:color s)
              nil]]))))))


