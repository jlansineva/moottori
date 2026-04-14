(ns pelinrakentaja-engine.graphics.renderer
  (:require [pelinrakentaja-engine.graphics.window :as window])
  (:import [org.lwjgl.opengl GL GL11]))

(defn init-renderer
  []
  (GL/createCapabilities)
  (GL11/glClearColor 1.0 0.0 0.0 1.0))

(defn render
  []
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (window/swap-buffers))
