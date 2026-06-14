(ns pelinrakentaja-engine.graphics.buffers
  (:require [pelinrakentaja-engine.core.state :as state])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack]
           [org.joml Matrix4f]))

(def usages {:static-draw GL32/GL_STATIC_DRAW})

(defn bind-buffer
  [engine-buffer-id]
  (let [{:keys [binding-target buffer-id]} (state/get-buffer-info engine-buffer-id)]
    (GL32/glBindBuffer binding-target buffer-id)))

(defn initialize-buffer
  [engine-buffer-id buffer-data & {:keys [usage]}]
  (let [stack (MemoryStack/stackPush)
        buffer-data-buffer (.mallocFloat stack (count buffer-data))
        buffer-id (GL32/glGenBuffers)]
    (state/add-buffer-info engine-buffer-id buffer-id :vbo GL32/GL_ARRAY_BUFFER usage)
    (doto buffer-data-buffer
      (.put buffer-data)
      (.flip))
    (GL32/glBindBuffer GL32/GL_ARRAY_BUFFER buffer-id)
    (GL32/glBufferData GL32/GL_ARRAY_BUFFER buffer-data (get usages usage))
    (MemoryStack/stackPop)))

(defn initialize-vbo
  [engine-vbo-id usage vertices]
  (initialize-buffer engine-vbo-id vertices :usage usage))
