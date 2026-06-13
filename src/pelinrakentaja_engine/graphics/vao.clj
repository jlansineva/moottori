(ns pelinrakentaja-engine.graphics.vao
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.core.state :as state])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack]
           [org.joml Matrix4f]))

(defn initialize-vao
  [engine-vao-id]
  (log/log :info :graphics/vao "Initializing VAO")
  (let [vao-id (GL32/glGenVertexArrays)]
    (state/add-vao-info engine-vao-id vao-id)
    (GL32/glBindVertexArray vao-id)))

(defn bind-vertex-array
  [engine-vao-id]
  (let [vao-info (state/get-vao-info engine-vao-id)]
    (GL32/glBindVertexArray (:vao-id vao-info))))
