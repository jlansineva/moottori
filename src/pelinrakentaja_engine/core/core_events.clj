(ns pelinrakentaja-engine.core.core-events
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.core.state :as state]))

(defn load-texture
  [{:keys [type texture] :as entity}]
  (state/load-texture type texture))

(defn add-entity
  [_state entity]
  {:pre [(some? (:x entity))
         (some? (:y entity))
         (some? (:type entity))]}
  (log/log :debug :event-handlers/add-entity entity)
  (state/add-entity entity))

(defn update-entity-with-id
  [_state entity-id entity]
  (log/log :debug :event-handlers/update-entity-with-id entity-id)
  (swap! state/renderable-entities
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
  (assoc-in state [:engine :cleanup?] true))

(defn engine-ready
  [state]
  (assoc-in state [:engine :ready?] true))

(defn engine-initialized
  [state]
  (assoc-in state [:engine :initialized?] true))

(defn key-down
  [state key-code]
  (assoc-in state [:input :keys key-code :pressed?] true))

(defn key-up
  [state key-code]
  (assoc-in state [:input :keys key-code :pressed?] false))
