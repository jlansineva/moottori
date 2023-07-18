(ns pelinrakentaja-engine.core.entities
  (:require [pelinrakentaja-engine.utils.log :as log]))

(defn create-entity
  [entity]
  (let [to-add (cond-> entity
                   true (update :x float)
                   true (update :y float)
                   (nil? (:id entity)) (assoc :id (keyword (gensym (name (:type entity))))))]
    (log/log :debug :add-entity to-add)
    to-add))

(defn create-entities
  [& entities]
  (mapv create-entity entities))
