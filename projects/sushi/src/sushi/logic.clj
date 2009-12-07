(ns sushi.logic)


; Define a function to send events
(defn event [k v] nil)

; Run a collection of components, start by sending an :init event to each component
(defn run-entities [events & ents]
  (loop [entities ents
         key :init
         value nil]
    (if (= key nil)
      ; Entity processing has completed - no new events have been generated
      entities
      ; Components have not completed processing - events are still left to be processed
      (let [updated ; Updated list of components
            ; Bind the event function to a closure which can update the vector of events
            (binding [event (fn [k v]
                              (swap! events
                                     concat
                                     (vector (list k v))))]
              ; Map the update function accross each component in parallel
              (doall (pmap #(conj %
                                  (if ((:handlers %) key)
                                    {:state (conj
                                              (:state %)
                                              (((:handlers %) key)

                                                   (:state %)))}
                                    {}))
                           entities)))]
        ; Recursively process components
        (recur updated ; Updated list of components
               (atom (rest @events)) ; Remaining events
               (ffirst @events) ; Next event type
               (second (first @events))))))) ; Next event message


