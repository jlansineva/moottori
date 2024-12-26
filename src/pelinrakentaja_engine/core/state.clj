(ns pelinrakentaja-engine.core.state)

(def initial-state
  {:engine
   {:status {:initialized? false
             :ready? false
             :cleanup? false}
    :graphics {:render-queue []
               :resource-load-queue []}}
   :entities {}})

(defonce engine-state (atom initial-state))

(defonce entity-state (atom {}))

(comment {:type :id
          :texture "some.png"}

         {:enemies {}
          :bullets {}
          :player {}
          :terrain {}
          :ui {}}

         [:terrain :enemies :bullets :player :ui])
