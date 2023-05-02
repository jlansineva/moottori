(ns pelinrakentaja-engine.core.state)

(defonce renderable-entities (atom {:id1 {:id :id1 :type :id :x (float 50) :y (float 50)}
                                    :id2 {:id :id2 :type :id :x (float 150) :y (float 150)}
                                    :id3 {:id :id3 :type :id :x (float 250) :y (float 250)}
                                    :id4 {:id :id4 :type :id :x (float 350) :y (float 350)}}))

(defonce render-queue (atom [:id1 :id2 :id4]))

(defn add-entity
  [entity]
  (let [to-add (-> entity
                 (update :x float)
                 (update :y float)
                 (assoc :id (keyword (gensym (name (:type entity))))))]
    (swap! renderable-entities assoc (:id to-add) to-add)
    (swap! render-queue conj (:id to-add))
    to-add))
