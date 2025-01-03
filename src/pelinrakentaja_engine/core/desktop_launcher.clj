(ns pelinrakentaja-engine.core.desktop-launcher
  (:require [pelinrakentaja-engine.core.game :refer :all]
            [pelinrakentaja-engine.core.debug-events :as debug-events]
            [pelinrakentaja-engine.core.core-events :as core-events]
            [pelinrakentaja-engine.core.core-listeners :as core-listeners]
            [pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.config :as config]
            [pelinrakentaja-engine.core.audio :as audio])
  (:import [com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration]
           [com.badlogic.gdx.graphics.glutils HdpiMode]
           #_[org.lwjgl3.input Keyboard])
  (:gen-class))

(defn setup-window
  [{:keys [title width height]}]
  (events/register-handler :input/key-down core-events/key-down)
  (events/register-handler :input/key-up core-events/key-up)

  (events/register-handler :input/mouse-down core-events/mouse-down)
  (events/register-handler :input/mouse-up core-events/mouse-up)
  (events/register-handler :input/mouse-moved core-events/mouse-moved) 

  (events/register-handler :resources/load-texture core-events/load-texture) ;; DEPRECATED
  (events/register-handler :resources/load-resource core-events/load-resource)
  (events/register-handler :entities/add-entities core-events/add-entities)
  (events/register-handler :engine/initialize core-events/engine-initialized)
  (events/register-handler :engine/ready core-events/engine-ready)
  (events/register-handler :engine/cleanup core-events/engine-cleanup)
  (events/register-handler :resources/load-resource-file core-events/load-resource-file)
  (events/register-handler :resources/resource-loaded core-events/resource-loaded)
  (events/register-handler :entities/update-entity-id-properties core-events/update-entity-id-properties)
  (events/register-handler :entities/update-entity-id-with-fn core-events/update-entity-id-with-fn)
  (events/register-handler :entities/update-entities-with-fn core-events/update-entities-with-fn)
  (events/register-handler :entities/update-entities-id-properties core-events/update-entities-id-properties)
  (events/register-handler :entities/remove-entity-with-id core-events/remove-entity-with-id)
  (events/register-handler :entities/remove-entities-with-ids core-events/remove-entities-with-ids)
  (events/register-handler :audio/play-music audio/play-music-with-id)

  ;; DEBUG
  (events/register-handler :debug/set-debug-mode debug-events/set-debug-mode)

  (events/register-listener :engine/status [:engine :status] core-listeners/engine-status)
  (events/register-listener :resources/resource-load-queue [:engine :graphics] core-listeners/resource-load-queue)
  (events/register-listener :input/pressed-keys [:input :keys] core-listeners/pressed-keys)
  (events/register-listener :engine/render-queue [:engine :graphics :render-queue] core-listeners/render-queue)
  (events/register-listener :engine/renderable-entities [:engine :entities] core-listeners/renderable-entities)
  (events/register-listener :resources/resource-loaded? [:resources] core-listeners/resource-loaded?)

  (events/register-listener :input/mouse-position [:input :mouse :position] core-listeners/mouse-position)
  (events/register-listener :input/mouse-pressed [:input :mouse] core-listeners/mouse-pressed)

  (let [configuration (Lwjgl3ApplicationConfiguration.)]
    (.setTitle configuration (or title "Game"))
    (.setResizable configuration false)
    (.setHdpiMode configuration HdpiMode/Logical)
    (.setWindowedMode configuration
                      (or width (config/get-config :window-default-x))
                      (or height (config/get-config :window-default-y)))
    (Lwjgl3Application. (pelinrakentaja-engine.core.game.Game.) configuration)))

(defn -main []
  (setup-window {})
  #_(Keyboard/enableRepeatEvents true))
