(ns pelinrakentaja-engine.core.core-listeners
  (:require [pelinrakentaja-engine.utils.log :as log]))

(defn engine-status
  [substate]
  substate)

(defn pressed-keys
  [substate]
  substate)

(defn entity-with-id
  [entity-id]
  )

(defn resource-load-queue
  [substate]
  (when-not (empty? substate)
    substate))

(def render-queue identity)
(def renderable-entities identity)
