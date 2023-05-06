(ns pelinrakentaja-engine.core.events)

(comment
  ;; event
  [::event-id :param1 :param2 :param3]


  )

(defonce state (atom {}))

(def event-queue
  "For storing events"
  (atom {:handlers {}
         :queue []}))

(defn create-handler
  [handler-fn]
  (fn -handler
    ([] (swap! state handler-fn))
    ([p1] (swap! state handler-fn p1))
    ([p1 p2] (swap! state handler-fn p1 p2))
    ([p1 p2 p3] (swap! state handler-fn p1 p2 p3))
    ([p1 p2 p3 & params] (apply swap! handler-fn p1 p2 p3 params))))

(defn register-handler
  [event-id handler-fn]
  (prn :register-handler event-id)
  (let [state-injected-handler (create-handler handler-fn)]
    (swap! event-queue assoc-in [:handlers event-id] state-injected-handler)))

(defn process-next-event
  [event-queue]
  (if (> (count (:queue event-queue)) 0)
    (let [[[event-id & parameters] & events] (:queue event-queue)]
      (if-let [handler (get-in (:handlers event-queue) [event-id])]
        (when (apply handler parameters)
          (assoc event-queue :queue (or events [])))
        (if event-id
          (throw (Exception. "whoops no handler"))
          (throw (Exception. "whoops empty queue")))))
    event-queue))

(defn update-queue
  []
  (swap! event-queue process-next-event))

(defn dispatch
  [event]
  {:pre [(vector? event)]}
  (prn :dispatch event @event-queue)
  (swap! event-queue #(update % :queue conj event)))
