(ns pelinrakentaja-engine.core.game
  (:require [pelinrakentaja-engine.core.state :as state])
  (:import [com.badlogic.gdx Gdx ApplicationAdapter]
           [com.badlogic.gdx.graphics GL20 OrthographicCamera Texture]
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
  (let [{:keys [camera batch] test-image :texture} @game-data
        font (BitmapFont.)
        text "Testing"]
    (.glClearColor (Gdx/gl) 0.2 0.2 0 0)
    (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
    (.update camera)
    (.setProjectionMatrix batch (.-combined camera))
    (.begin batch)
    (doseq [entity-id @state/render-queue]
      (let [entity (get @state/renderable-entities entity-id)]
        (.draw batch test-image (:x entity) (:y entity))))
    (.end batch)
    (.begin batch)
    (. font draw batch text (float 200) (float 200))
    (.end batch)))

(defn -create [^ApplicationAdapter this]
  (let [camera (OrthographicCamera.)
        sprite-batch (SpriteBatch.)
        test-image (Texture. (.internal (. Gdx -files) "some.png"))]
    (.setToOrtho camera false 800 480)
    (swap! game-data assoc
      :texture test-image
      :camera camera
      :batch sprite-batch)))
