(ns pelinrakentaja-engine.graphics.renderer
  (:require [pelinrakentaja-engine.graphics.window :as window]
            [pelinrakentaja-engine.graphics.sprite-manager :as sprite-manager]
            [pelinrakentaja-engine.config :as config]
            [pelinrakentaja-engine.utils.log :as log])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack])
  )



(defn init-renderer
  []
  (log/log log/default-log-level :engine/run "Creating capabilities")
  (GL/createCapabilities)
  (log/log log/default-log-level :engine/run "Set clear color")
  (GL32/glClearColor 0.2 0.0 0.0 0.0)
  (log/log log/default-log-level :engine/run "Create viewport")
  (GL32/glViewport 0 0 (config/get-config :window-default-x) (config/get-config :window-default-y))
  (log/log log/default-log-level :engine/run "Setting up sprite manager")
  (sprite-manager/initialize))

(defn render
  []

  (GL32/glClear (bit-or GL32/GL_COLOR_BUFFER_BIT GL32/GL_DEPTH_BUFFER_BIT))

  (sprite-manager/render)

  (window/swap-buffers))
