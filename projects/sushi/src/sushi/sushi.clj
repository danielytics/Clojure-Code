(ns sushi.sushi
  (:use core
        logic))

(defmacro on [event header & body]
  {event `(fn ~header ~@body)})

(defstruct entity-s :name :handlers)
(defn entity [name & handlers]
  "Create a single game entity"
  (struct entity-s name handlers))

(defn entities [& entity-list]
  "Create a list of entities"
  entity-list)

(defn on-keypress [key event param]
  "Trigger an event when this key is pressed"
  (list :press (symbol (str "Keyboard/" key)) (list event param)))

(defn on-keyrelease [key event param]
  "Trigger an event when this key is released"
  (list :up (symbol (str "Keyboard/" key)) (list event param)))

(defn on-keydown [key event param]
  "Trigger an event while this key is down"
  (list :down (symbol (str "Keyboard/" key)) (list event param)))

(defn map-input [& input]
  "Create a list of input mappings"
  input)

(defstruct state-s :name :input-map :entities)
(defn state [name input-map entities]
  "Create a single state object"
  (struct state-s name input-map entities))

(defn states [& states]
  "Create a map of states"
  (loop [state-map {}
         head      (first states)
         tail     (rest states)]
    (if (= head nil)
      state-map
      (recur (conj state-map {(:name head) head})
             (first tail)
             (rest tail)))))

(def start-state identity)

(defn game [name start states]
  (run-game name
            start
            states
            logic-func
            render-func))

