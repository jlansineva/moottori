(ns pelinrakentaja-engine.core.state
  (:require [pelinrakentaja-engine.core.graphics.textures :as textures]))

(defonce renderable-entities
  (atom {}))

(defonce render-queue
  (atom []))

(defn add-entity
  [entity]
  (let [to-add (-> entity
                 (update :x float)
                 (update :y float)
                 (assoc :id (keyword (gensym (name (:type entity))))))]
    (when (get @textures/textures-for-type (:type entity))
      (swap! renderable-entities assoc (:id to-add) to-add)
      (swap! render-queue conj (:id to-add))
      to-add)))

(defn add-entities
  [& entities]
  (mapv add-entity entities))

(comment {:type :id
          :texture "some.png"}

         {:enemies {}
          :bullets {}
          :player {}
          :terrain {}
          :ui {}}

         [:terrain :enemies :bullets :player :ui])

#_(defn load-entity
  [entity]
  (let [{:keys [type texture]} entity]
    (load-texture type texture)))
