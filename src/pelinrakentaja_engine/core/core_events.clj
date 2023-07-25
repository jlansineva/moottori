(ns pelinrakentaja-engine.core.core-events
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.core.entities :as entities]
            [pelinrakentaja-engine.core.graphics.textures :as textures]))

(defn load-texture
  "Loading of the texture resources have to be done from within the libGDX-thread, so resources
  have to be added to a queue."
  [state {:keys [type texture] :as entity}]
  (update-in state [:engine :graphics :resource-load-queue] conj {:id type :path texture :type :texture}))

(def supported-resources #{:texture})

(defn resource-loaded
  "Takes the first resource off the resource load queue"
  [state resource-type resource-id resource]
  {:pre [(supported-resources resource-type)]}
  (log/log :debug :resource-loaded resource-id (get-in state [:engine :graphics :resource-load-queue]))
  (-> state
      (assoc-in [:resources resource-type resource-id] resource)
      (update-in [:engine :graphics :resource-load-queue] (comp vec rest))))

(defn load-resource
  [state id path type]
  (let [region (textures/load-texture-from-resource id path)]
    (resource-loaded state type id region)))

(defn add-entity
  [state entity]
  {:pre [(some? (:x entity))
         (some? (:y entity))
         (some? (:type entity))]}
  (log/log :debug :event-handlers/add-entity entity)
  (let [new-entity (entities/create-entity entity)]
    (cond-> state
        new-entity (update-in [:engine :graphics :render-queue] conj (:id new-entity))
        new-entity (assoc-in [:engine :entities (:id new-entity)] new-entity))))

(defn add-entities
  [state & entities]
  (reduce add-entity
          state
          entities))

(defn update-entity-id-with-fn
  [state entity-id modifier-fn]
  (log/log :debug :event-handlers/update-entity-id-with-fn entity-id)
  (update-in state
             [:engine :entities entity-id]
             modifier-fn))

(defn update-entity-id-properties
  [state entity-id entity properties]
  (log/log :debug :event-handlers/update-entity-with-id entity-id)
  (let [old-entity (get-in state [:engine :entities entity-id])
        updated-entity (merge old-entity (select-keys entity properties))]
    (assoc-in state
              [:engine :entities entity-id]
              updated-entity)))

(defn update-entities-with-fn
  [state fn]
  (update-in state [:engine :entities] fn))

(defn update-entities-id-properties
  [state & entities]
  (reduce (fn [state [entity-id entity properties]]
            (update-entity-id-properties state entity-id entity properties)) state entities))

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
