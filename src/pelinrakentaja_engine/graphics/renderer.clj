(ns pelinrakentaja-engine.graphics.renderer
  (:require [pelinrakentaja-engine.graphics.window :as window])
  (:import [org.lwjgl.opengl GL GL30]))

(def vao (atom nil))

(defn init-renderer
  []
  (GL/createCapabilities)
  (GL30/glClearColor 1.0 0.0 0.0 1.0)
  #_#_(swap! vao GL30/glGenVertexArrays)
  (GL30/glBindVertexArray @vao))

(defn render
  []

  (GL30/glClear (bit-or GL30/GL_COLOR_BUFFER_BIT GL30/GL_DEPTH_BUFFER_BIT))



  (window/swap-buffers))
