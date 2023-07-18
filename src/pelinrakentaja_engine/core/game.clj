(ns pelinrakentaja-engine.core.game
  (:require [pelinrakentaja-engine.core.state :as state]
            [pelinrakentaja-engine.core.input :as input]
            [pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.core.graphics.textures :as textures]

            [pelinrakentaja-engine.utils.log :as log])
  (:import [com.badlogic.gdx Gdx ApplicationAdapter]
           [com.badlogic.gdx.graphics GL20 OrthographicCamera]
           [com.badlogic.gdx.graphics.g2d BitmapFont SpriteBatch]))

(gen-class
  :name pelinrakentaja-engine.core.game.Game
  :extends com.badlogic.gdx.ApplicationAdapter)

(defonce game-data (atom {}))

(def resource-queue-listener (events/listener :resources/resource-load-queue))
(def render-queue (events/listener :engine/render-queue))
(def renderable-entities (events/listener :engine/renderable-entities))

(defn -render
  [^ApplicationAdapter this]
  (let [{:keys [camera batch]} @game-data
        font (BitmapFont.)
        text "Testing"
        resource-load-queue (resource-queue-listener)
        render-q (render-queue)
        entities (renderable-entities)]
    (when resource-load-queue
      (let [{:keys [id path]} (first resource-load-queue)]
        (textures/load-texture-from-resource id path)))
    (log/log :debug :events render-q)
    (.glClearColor (Gdx/gl) 0.2 0.2 0 0)
    (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
    (.update camera)
    (.setProjectionMatrix batch (.-combined camera))
    (.begin batch)
    (doseq [entity-id render-q]
      (let [{{:keys [width height]
              tex-x :x
              tex-y :y} :texture :as entity} (get entities entity-id)]
        (when-let [texture (get-in @state/engine-state [:resources :texture (:type entity)])]
          (log/log :debug :events texture)
          (.setRegion texture
                      (or tex-x 0) (or tex-y 0)
                      (or width (-> texture .getTexture .getWidth))
                      (or height (-> texture .getTexture .getHeight)))
          (.draw batch texture (:x entity) (:y entity)))))
    (.end batch)
    (.begin batch)
    (. font draw batch text (float 200) (float 200))
    (.end batch)))

(defn -create
  [^ApplicationAdapter this]
  (log/log :debug :engine/lifecycle "creating")
  (let [cam-w (.getWidth (. Gdx -graphics))
        cam-h (.getHeight (. Gdx -graphics))
        camera (OrthographicCamera. 100, (* 100 (/ cam-h cam-w)))
        sprite-batch (SpriteBatch.)]
    (.setInputProcessor (. Gdx -input) (input/create-input-adapter))
    (.setToOrtho camera false cam-w cam-h) ;; TODO something sensible to these, maybe use a viewport
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
