(ns pelinrakentaja-engine.core.game
  (:require [pelinrakentaja-engine.core.state :as state]
            [pelinrakentaja-engine.core.input :as input]
            [pelinrakentaja-engine.core.events :as events])
  (:import [com.badlogic.gdx Gdx ApplicationAdapter]
           [com.badlogic.gdx.graphics GL20 OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont SpriteBatch]))

(gen-class
  :name pelinrakentaja-engine.core.game.Game
  :extends com.badlogic.gdx.ApplicationAdapter)

(defonce game-data (atom {}))

#_(def main-screen
  (let [stage (atom nil)]
    (proxy [Screen] []
      (show []
        (reset! stage (Stage.))
        (let []
          (.addActor @stage label)))
      (render [delta]
        (.glClearColor (Gdx/gl) 0 0 0 0)
        (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
        (doto @stage
          (.act delta)
          (.draw)))
      (dispose[])
      (hide [])
      (pause [])
      (resize [w h])
      (resume []))))

(defn -render [^ApplicationAdapter this]
  (let [{:keys [camera batch]} @game-data
        font (BitmapFont.)
        text "Testing"]
    (.glClearColor (Gdx/gl) 0.2 0.2 0 0)
    (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
    (.update camera)
    (.setProjectionMatrix batch (.-combined camera))
    (.begin batch)
    (doseq [entity-id @state/render-queue]
      (let [entity (get @state/renderable-entities entity-id)
            texture (get @state/textures-for-type (:type entity))]
        (.draw batch texture (:x entity) (:y entity))))
    (.end batch)
    (.begin batch)
    (. font draw batch text (float 200) (float 200))
    (.end batch)))

(defn -create [^ApplicationAdapter this]
  (let [camera (OrthographicCamera.)
        sprite-batch (SpriteBatch.)]
    (events/register-handler :input/key-down
                             (fn [state key-code]
                               (prn :> :key-down)
                               (swap! state/renderable-entities
                                      (fn [entities]
                                        (into {} (mapv (fn [[id entity]]
                                                         [id (update entity :x #(float (inc %)))]) entities))))))
    (events/register-handler :input/key-up
                             (fn [state key-code]
                               (prn :> :key-up)
                               (swap! state/renderable-entities
                                      (fn [entities]
                                        (into {} (mapv (fn [[id entity]]
                                                         [id (update entity :x #(float (inc %)))]) entities))))))
    (state/load-entity {:type :id :texture "some.png"})
    (state/add-entities
      {:x 34 :y 56 :type :id}
      {:x 134 :y 56 :type :id}
      {:x 34 :y 156 :type :id}
      {:x 234 :y 56 :type :id})
    (.setInputProcessor (. Gdx -input) (input/create-input-adapter))
    (.setToOrtho camera false 800 480)
    (swap! game-data assoc
      :camera camera
      :batch sprite-batch)))
