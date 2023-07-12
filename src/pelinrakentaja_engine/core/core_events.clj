(ns pelinrakentaja-engine.core.core-events
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.core.state :as s]))

(defn load-texture
  "Loading of the texture resources have to be done from within the libGDX-thread, so resources
  have to be added to a queue."
  [state {:keys [type texture] :as entity}]
  (update-in state [:engine :graphics :resource-load-queue] conj {:id type :path texture}))

(def supported-resources #{:texture})

(defn resource-loaded
  "Takes the first resource off the resource load queue"
  [state resource-type resource-id resource]
  {:pre [(supported-resources resource-type)]}
  (-> state
      (assoc-in [:resources resource-type resource-id] resource)
      (update-in [:engine :graphics :resource-load-queue] rest)
      (update-in [:engine :graphics :resource-load-queue] vec)))

(defn add-entity
  [state entity]
  {:pre [(some? (:x entity))
         (some? (:y entity))
         (some? (:type entity))]}
  (log/log :debug :event-handlers/add-entity entity)
  (s/add-entity entity)
  state)

(defn add-entities
  [state & entities]
  (doseq [e entities]
    (add-entity state e))
  state)

(defn update-entity-with-id
  [_state entity-id entity]
  (log/log :debug :event-handlers/update-entity-with-id entity-id)
  (swap! s/renderable-entities
         (fn [entities]
           (loop [current (first entities)
                  remaining (rest entities)
                  processed []]

             (cond (nil? current)
                   processed

                   (= (:id current) entity-id)
                   (vec (concat processed [entity] remaining))

                   :else
                   (recur (first remaining)
                          (rest remaining)
                          (conj processed current)))))))

(defn engine-cleanup
  [state]
  (log/log :debug :event-handlers/engine-cleanup state)
  (assoc-in state [:engine :status :cleanup?] true))

(defn engine-ready
  [state]
  (log/log :debug :event-handlers/engine-ready state)
  (assoc-in state [:engine :status :ready?] true))

(defn engine-initialized
  [state]
  (log/log :debug :event-handlers/engine-initialized state)
  (assoc-in state [:engine :status :initialized?] true))

(defn key-down
  [state key-code]
  (assoc-in state [:input :keys key-code :pressed?] true))

(defn key-up
  [state key-code]
  (assoc-in state [:input :keys key-code :pressed?] false))
