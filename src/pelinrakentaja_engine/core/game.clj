(ns pelinrakentaja-engine.core.game
  (:require [pelinrakentaja-engine.core.state :as state]
            [pelinrakentaja-engine.core.input :as input]
            [pelinrakentaja-engine.core.events :as events]

            [pelinrakentaja-engine.utils.log :as log])
  (:import [com.badlogic.gdx Gdx ApplicationAdapter]
           [com.badlogic.gdx.graphics GL20 OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont SpriteBatch]))

(gen-class
  :name pelinrakentaja-engine.core.game.Game
  :extends com.badlogic.gdx.ApplicationAdapter)

(defonce game-data (atom {}))

(defn -render
  [^ApplicationAdapter this]
  (let [{:keys [camera batch]} @game-data
        font (BitmapFont.)
        text "Testing"]
    #_(log/log :debug :events (:engine @events/state))
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

(defn -create
  [^ApplicationAdapter this]
  (log/log :debug :engine/lifecycle "creating")
  (let [camera (OrthographicCamera.)
        sprite-batch (SpriteBatch.)]
    (.setInputProcessor (. Gdx -input) (input/create-input-adapter))
    (.setToOrtho camera false 800 480)
    (swap! game-data assoc
           :camera camera
           :batch sprite-batch)
    (events/force [:engine/initialize])))

(defn -pause
  [^ApplicationAdapter this]
  (log/log :debug :engine/lifecycle "paused"))

(defn -dispose
  [^ApplicationAdapter this]
  (events/dispatch [:engine/cleanup])
  (log/log :debug :engine/lifecycle "disposed"))
