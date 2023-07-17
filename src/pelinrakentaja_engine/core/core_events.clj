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
  (let [new-entity (s/create-entity entity)]
    (cond-> state
        new-entity (update-in [:engine :graphics :render-queue] conj (:id new-entity))
        new-entity (assoc-in [:engine :entities (:id new-entity)] new-entity))))

(defn add-entities
  [state & entities]
  (reduce add-entity
          state
          entities))

(defn update-entity-with-id
  [state entity-id entity]
  (log/log :debug :event-handlers/update-entity-with-id entity-id)
  (update-in state
             [:engine :entities]
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

(defn update-entities-with-fn
  [state fn]
  (update-in state [:engine :entities] fn))

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
