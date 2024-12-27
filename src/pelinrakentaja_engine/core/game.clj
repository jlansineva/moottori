(ns pelinrakentaja-engine.core.game
  (:require [pelinrakentaja-engine.core.graphics.camera :as graphics.camera]
            [pelinrakentaja-engine.config :as config]
            [pelinrakentaja-engine.core.state :as state]
            [pelinrakentaja-engine.core.input :as input]
            [pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.core.graphics.textures :as textures]
            [pelinrakentaja-engine.utils.log :as log])
  (:import [com.badlogic.gdx Gdx ApplicationAdapter]
           [com.badlogic.gdx.graphics GL20 OrthographicCamera]
           [com.badlogic.gdx.utils.viewport ExtendViewport]
           [com.badlogic.gdx.graphics.g2d BitmapFont SpriteBatch]))

"What should happen is that currently the render queue is a collection of entity ids to be drawn.

Renderable entities is a collection of entity data corresponding to ids with x and y and texture data.

These are stored in state.

There is a barrier between entities in the renderer and entities in the game.

These should be the same.

The render queue is just all entities."

(gen-class
  :name pelinrakentaja-engine.core.game.Game
  :extends com.badlogic.gdx.ApplicationAdapter)

(defonce game-data (atom {}))

(def resource-queue-listener (events/listener :resources/resource-load-queue))
(def render-queue (events/listener :engine/render-queue))
(def renderable-entities (events/listener :engine/renderable-entities))

(defn -render
  [^ApplicationAdapter this]
  (let [{{:keys [active-camera]} :cameras :keys [batch viewport]} @game-data
        resource-load-queue (resource-queue-listener)
        render-q (render-queue)
        entities (renderable-entities)]
    (when resource-load-queue
      (let [{:keys [id path type]} (first resource-load-queue)]
        (log/log :debug :resource id path)
        (events/direct-state-access [:resources/load-resource-file id path type])))
    #_(log/log :debug :events render-q)
    ;; TODO: camera should be its own entity that is controlled from outside
    (graphics.camera/move-camera active-camera 0.01 0.005 0)
    (.glClearColor (Gdx/gl) 0.2 0.2 0 0)
    (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
    (.update (:camera active-camera))
    (.setProjectionMatrix batch (.-combined (:camera active-camera)))
    (.begin batch)
    (doseq [entity-id render-q]
      (let [{{:keys [width height]} :texture ;; TODO: implement scale and rotation
             {_scale-x :x _scale-y :y} :scale
             ent-x :x
             ent-y :y :as entity
             :keys [_rotation]} (get entities entity-id)]
        (when-let [textureregion (get-in @state/engine-state [:resources :texture (:type entity)])]
          (.draw batch
                 (.getTexture textureregion)
                 ent-x (- (config/get-config :world-height) ent-y) ;; <todo fix - this needs to be toggled somehow
                 width (- height)
                 0 0
                 width height))))
    (.end batch)))

(defn -create
  [^ApplicationAdapter this]
  (log/log :debug :engine/lifecycle "creating")
  (let [cam-w (config/get-config :cam-width) ;; y = 28, x = 92
        cam-h (config/get-config :cam-height)
        camera (graphics.camera/create-camera :orthographic)
        viewport (ExtendViewport. cam-w cam-h (:camera camera))
        sprite-batch (SpriteBatch.)]
    (.setInputProcessor (. Gdx -input) (input/create-input-adapter))
    (.apply viewport)
    (graphics.camera/legacy-init camera viewport)
    (swap! game-data assoc
           :cameras {:active-camera camera
                     :created-cameras [camera]}
           :viewport viewport
           :batch sprite-batch)
    (events/force [:engine/initialize])))

(defn -pause
  [^ApplicationAdapter this]
  (log/log :debug :engine/lifecycle "paused"))

(defn -dispose
  [^ApplicationAdapter this]
  (events/dispatch [:engine/cleanup])
  (log/log :debug :engine/lifecycle "disposed"))

(defn -resize
  [^ApplicationAdapter this width height]
  (.update (:viewport @game-data) width height))
