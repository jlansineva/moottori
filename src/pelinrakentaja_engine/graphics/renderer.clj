(ns pelinrakentaja-engine.graphics.renderer
  (:require [pelinrakentaja-engine.graphics.window :as window]
            [pelinrakentaja-engine.graphics.sprite :as sprite])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack])
  )



(defn init-renderer
  []
  (GL/createCapabilities)
  (GL32/glClearColor 0.2 0.0 0.0 0.0)
  (GL32/glViewport 0 0 640 480)
  (sprite/initialize-sprite-core))

(defn render
  []

  (GL32/glClear (bit-or GL32/GL_COLOR_BUFFER_BIT GL32/GL_DEPTH_BUFFER_BIT))

  (sprite/render-sprite)

  (window/swap-buffers))
