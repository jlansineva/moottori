(ns pelinrakentaja-engine.core.core-events
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.core.entities :as entities]
            [pelinrakentaja-engine.core.graphics.textures :as textures]
            [pelinrakentaja-engine.core.audio :as audio]))

(defn load-resource
  "General loading method for resources"
  [state resource]
  {:pre [(some? (:type resource))
         (or (some? (:texture resource))
             (some? (:music resource))
             (some? (:sfx resource)))]}
  (let [resource-type (cond
                        (some? (:texture resource)) :texture
                        (some? (:music resource)) :music
                        (some? (:sfx resource)) :sfx)]
    (update-in state
               [:engine :graphics :resource-load-queue]
               conj
               {:id (:type resource)
                :path (get resource resource-type)
                :type resource-type})))

(defn load-texture
  "Loading of the texture resources have to be done from within the libGDX-thread, so resources
  have to be added to a queue."
  [state {:keys [type texture] :as entity}]
  (update-in state [:engine :graphics :resource-load-queue] conj {:id type :path texture :type :texture}))

(def supported-resources #{:texture :music :sfx})

(defn resource-loaded
  "Takes the first resource off the resource load queue"
  [state resource-type resource-id resource]
  {:pre [(supported-resources resource-type)]}
  (log/log :debug :resource-loaded resource-id (get-in state [:engine :graphics :resource-load-queue]))
  #_(log/log :debug :resource-loaded resource-id (get-in state [:resources]))
  #_(log/log :debug :resource-loaded resource-id resource resource-type)
  (cond-> state
        (some? resource) (assoc-in [:resources resource-type resource-id] resource)
        true (update-in [:engine :graphics :resource-load-queue] (comp vec rest))))

(defn load-resource-file
  [state id path type]
  (let [resource-file (case type
                        :texture (textures/load-texture-from-resource id path)
                        :music (audio/load-music-from-resources id path)
                        :sfx (audio/load-sfx-from-resources id path))]
    (resource-loaded state type id resource-file)))

(defn add-entity
  [state entity]
  {:pre [(some? (:x entity))
         (some? (:y entity))
         (some? (:type entity))]}
  (let [new-entity (entities/create-entity entity)]
    (log/log :debug :event-handlers/add-entity entity)
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

(defn remove-entity-with-id
  [state entity-id]
  (-> state
      (update-in [:engine :entities] dissoc entity-id)
      (update-in [:engine :graphics :render-queue] #(vec (remove (fn [id] (= id entity-id)) %)))))

(defn remove-entities-with-ids
  [state & entities]
  (log/log :debug :event-handlers/remove-entities-with-ids entities)
  (reduce remove-entity-with-id state entities))

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
