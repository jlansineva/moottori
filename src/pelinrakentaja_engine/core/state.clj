(ns pelinrakentaja-engine.core.state)

(def initial-state
  {:engine
   {:status {:initialized? false
             :ready? false
             :cleanup? false}
    :graphics {:render-queue []}}
   :entities {}})

(defonce engine-state (atom initial-state))

(comment {:type :id
          :texture "some.png"}

         {:enemies {}
          :bullets {}
          :player {}
          :terrain {}
          :ui {}}

         [:terrain :enemies :bullets :player :ui])
