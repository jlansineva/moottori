(ns pelinrakentaja-engine.core.desktop-launcher
  (:require [pelinrakentaja-engine.core.game :refer :all]
            [pelinrakentaja-engine.core.core-events :as core-events]
            [pelinrakentaja-engine.core.core-listeners :as core-listeners]
            [pelinrakentaja-engine.core.events :as events])
  (:import [com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration]
           #_[org.lwjgl3.input Keyboard])
  (:gen-class))

(defn setup-window
  [{:keys [title width height]}]
  (events/register-handler :input/key-down core-events/key-down)
  (events/register-handler :input/key-up core-events/key-up)
  (events/register-handler :resources/load-texture core-events/load-texture)
  (events/register-handler :entities/add-entities core-events/add-entities)
  (events/register-handler :engine/initialize core-events/engine-initialized)
  (events/register-handler :engine/ready core-events/engine-ready)
  (events/register-handler :engine/cleanup core-events/engine-cleanup)
  (events/register-handler :resources/resource-loaded core-events/resource-loaded)
  (events/register-handler :entities/update-entity-with-id core-events/update-entity-with-id)
  (events/register-handler :entities/update-entities-with-fn core-events/update-entities-with-fn)

  (events/register-listener :engine/status [:engine :status] core-listeners/engine-status)
  (events/register-listener :resources/resource-load-queue [:engine :graphics :resource-load-queue] core-listeners/resource-load-queue)
  (events/register-listener :input/pressed-keys [:input :keys] core-listeners/pressed-keys)
  (events/register-listener :engine/render-queue [:engine :graphics :render-queue] core-listeners/render-queue)
  (events/register-listener :engine/renderable-entities [:engine :entities] core-listeners/renderable-entities)
  (let [config (Lwjgl3ApplicationConfiguration.)]
    (.setTitle config (or title "Game"))
    (.setWindowedMode config (or width 1024) (or height 768))
    (Lwjgl3Application. (pelinrakentaja-engine.core.game.Game.) config)))

(defn -main []
  (setup-window {})
  #_(Keyboard/enableRepeatEvents true))
