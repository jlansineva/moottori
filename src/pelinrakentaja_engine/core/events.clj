(ns pelinrakentaja-engine.core.events
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.core.state :as state]))

(comment
  ;; event
  [::event-id :param1 :param2 :param3]


  )


(defonce event-queue
  (atom {:handlers {}
         :listeners {}
         :affected {}
         :queue []}))

(comment
  ;; handler affects path
  ;; check for path if there is a listener
  ;; toggle a dirty flag (or modified flag)
  ;; if dirty flag is true, update listeners and remove flag
  ;;
  {:handlers {:id :handler
              :id2 :handler2}
   :affected {:id true
              :id2 false}
   :listeners {:paths [[[:path :to :state] :id]
                       [[:path :to :another] :id2]]
               :fns {:id {:fn (fn [state] (prn state))
                          :path [:path :to :state]
                          :fx (fn [state] (assoc state :a :b))}
                     :id2 {:fn (fn [state] (prn state))
                           :path [:path :to :else]}}}}

  {:input {:keys {:a true}}})

(defn add-listener
  [queue-state id path listener-fn]
  (-> queue-state
      (update-in [:listeners :paths] conj [path id])
      (assoc-in [:listeners :fns id] {:fn listener-fn
                                      :path path}))) ;; TODO: check if path exists

(defn remove-listener
  [queue-state id]
  (let [remove-from-paths #(not= id (second %))]
    (-> queue-state
        (update-in [:listeners :paths] #(filter remove-from-paths %))
        (update-in [:listeners :fns] dissoc id))))


(defn register-listener
  "Registers a listener. A listener has an id that it is referred to by.
  A listener is also given a path that it observes. Should the path be affected,
  a listener will return a new value. Otherwise it will return nil."
  [id path listener-fn]
  (log/log :debug :listener/register id)
  (swap! event-queue add-listener id path listener-fn))

(defn clear-listener
  [id]
  (swap! event-queue remove-listener id))

(defn listener
  "If a listener is registered and the path is affected, the listener is called with the new state"
  [listener-id]
  (fn -listener
    [& params]
    (if-let [l (get-in @event-queue [:listeners :fns listener-id])]
      (let [path (get l :path)
            listener-fn (get l :fn)
            returnable (apply listener-fn (get-in @state/engine-state path) params)]
        #_(when (get-in @event-queue [:affected listener-id]))
        returnable)
      (when (get-in @state/engine-state [:engine :status :initialized?])
        (throw (Exception. (str "whoops no listener " listener-id (pr-str (:listeners @event-queue))))))))) ;; TODO error logging

(defn create-handler
  [handler-fn]
  (fn -handler
    ([] (swap! state/engine-state handler-fn))
    ([p1] (swap! state/engine-state handler-fn p1))
    ([p1 p2] (swap! state/engine-state handler-fn p1 p2))
    ([p1 p2 p3] (swap! state/engine-state handler-fn p1 p2 p3))
    ([p1 p2 p3 & params] (apply swap! state/engine-state handler-fn p1 p2 p3 params))))

(defn register-handler
  [event-id handler-fn]
  (log/log :debug :register-handler event-id) ;; TODO use logging
  (let [state-injected-handler (create-handler handler-fn)]
    (swap! event-queue assoc-in [:handlers event-id] state-injected-handler)))

(defn process-next-event
  [event-queue]
  (if (> (count (:queue event-queue)) 0)
    (assoc
     event-queue
     :queue
     (loop [queue (:queue event-queue)]
       (if (empty? queue)
         queue
         (recur
          (let [[[event-id & parameters] & events] queue]
            (if-let [handler (get-in (:handlers event-queue) [event-id])]
              (when (apply handler parameters)
                (vec events))
              (if event-id
                (throw (Exception. (str "whoops no handler " event-id))) ;; TODO ERROR LOGGING
                (throw (Exception. "whoops empty queue"))))))))) ;; TODO ERROR LOGGING
    event-queue))

(defn update-queue
  ([]
   (update-queue false))
  ([force?]
   (log/log :debug :event/queue-update "updating queue")
   (when (or (get-in @state/engine-state [:engine :status :initialized?]) force?)
     (swap! event-queue process-next-event))))

(defn direct-state-access
  [[event & params]]
  (let [handler-fn (get-in @event-queue [:handlers event])]
    (prn :> :debug :direct-access event params (get-in @event-queue [:handlers]))
    (apply handler-fn params)))

(defn force
  [event]
  {:pre [(vector? event)]}
  (log/log :debug :event/force event @event-queue)
  (swap! event-queue #(update % :queue conj event))
  (update-queue true))

(defn dispatch
  [event]
  {:pre [(vector? event)]}
  (log/log :debug :event/dispatch event @event-queue)
  (swap! event-queue #(update % :queue conj event)))
