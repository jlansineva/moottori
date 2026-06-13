(ns pelinrakentaja-engine.core.state)

(def initial-state
  {:engine
   {:status {:initialized? false
             :ready? false
             :cleanup? false}
    :graphics {:render-queue []
               :resource-load-queue []}
    :log []}
   :entities {}})

(defonce engine-state (atom initial-state))

(defonce entity-state (atom {}))

(defn set-engine-state-path
  [path value]
  (let [-set-state-path-value (fn [engine-state]
                                (let [old-val (get-in engine-state path)]
                                  (-> engine-state
                                      (update-in [:engine :log] conj {:path path :change [old-val :-> value]})
                                      (assoc-in path value))))]
    (swap! engine-state -set-state-path-value)))

(defn get-engine-state-path
  [path]
  (get-in @engine-state path))

(defn add-vao-info
  [engine-id vao-id]
  (set-engine-state-path [:engine :graphics :vaos engine-id] {:vao-id vao-id}))

(defn get-vao-info
  [engine-id]
  (get-engine-state-path [:engine :graphics :vaos engine-id]))

(comment {:type :id
          :texture "some.png"}

         {:enemies {}
          :bullets {}
          :player {}
          :terrain {}
          :ui {}}

         [:terrain :enemies :bullets :player :ui])
