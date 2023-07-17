(ns pelinrakentaja-engine.core.state
  (:require [pelinrakentaja-engine.utils.log :as log]))

(def initial-state
  {:engine
   {:status {:initialized? false
             :ready? false
             :cleanup? false}
    :graphics {:render-queue []}}
   :entities {}})

(defonce engine-state (atom initial-state))

(defn create-entity
  [entity]
  (let [to-add (-> entity
                   (update :x float)
                   (update :y float)
                   (assoc :id (keyword (gensym (name (:type entity))))))]
    (log/log :debug :add-entity @engine-state)
    (when (get-in @engine-state [:resources :texture (:type entity)])
      to-add)))

(defn create-entities
  [& entities]
  (mapv create-entity entities))

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
