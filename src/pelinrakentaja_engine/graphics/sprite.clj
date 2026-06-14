(ns pelinrakentaja-engine.graphics.sprite
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.graphics.camera :as camera]
            [pelinrakentaja-engine.graphics.shaders :as shaders]
            [pelinrakentaja-engine.graphics.vao :as vao]
            [pelinrakentaja-engine.graphics.buffers :as buffers])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack]
           [org.joml Matrix4f]))

(def sprite-vertices [0.0 1.0 -1.0 1.0 0.0 0.0
                      0.0 0.0 -1.0 0.0 1.0 0.0
                      1.0 1.0 -1.0 0.0 0.0 1.0
                      1.0 0.0 -1.0 1.0 1.0 0.0])

(defn initialize-vbo
  []
  (buffers/initialize-vbo ::sprite-vertex-buffer :static-draw (float-array sprite-vertices))
  ;; TODO: add possibility to generate VBOs for more different quads.
  )

(defn compile-shaders
  []
  (let [{:keys [vertex fragment program]}
        (shaders/create-shader
          :shaders [:fragment :vertex :program]
          :glsl {:fragment shaders/fragment-shader-basic
                 :vertex shaders/vertex-shader-basic}
          :bindings {:fragdata-location {:color-number 0
                                         :out-name "fragColor"}})

        [model-location view-location projection-location] (shaders/get-uniform-locations program "model" "view" "projection")]
    (shaders/store-shader ::default
                                         :shader-program-id program
                                         :fragment-shader-id fragment
                                         :vertex-shader-id vertex
                                         :model-uniform-location model-location
                                         :view-uniform-location view-location
                                         :projection-uniform-location projection-location)
    (shaders/set-attribute-pointer-in-vao-for-location program "position")
    (shaders/set-attribute-pointer-in-vao-for-location program "color"))

;; TODO: initializes just a single quad type. It might be useful to provide options for a set of quads in a single VBO
  )
(defn initialize-sprite-core
  []
  (log/log log/default-log-level :engine/sprite "Initialize VAO")
  (vao/initialize-vao ::sprite)
  (log/log log/default-log-level :engine/sprite "Initialize VBO")
  (initialize-vbo)
  (log/log log/default-log-level :engine/sprite "Initialize Shaders")
  (compile-shaders))

(defn render-sprite
  [context]
  (vao/bind-vertex-array ::sprite)
  (buffers/bind-buffer ::sprite-vertex-buffer)

  (doseq [s context]
    (let [{:keys [shader-program-id
                  model-uniform-location
                  view-uniform-location
                  projection-uniform-location
                  model-matrix]} s]
      (shaders/use-program shader-program-id)

      (let [stack (MemoryStack/stackPush)]
          ;; TODO: add camera API functions
        (GL32/glUniformMatrix4fv model-uniform-location false (.get model-matrix (.mallocFloat stack 16)))
        (GL32/glUniformMatrix4fv view-uniform-location false (.get (:transform @camera/camera) (.mallocFloat stack 16)))
        (GL32/glUniformMatrix4fv projection-uniform-location false (.get (:projection @camera/camera) (.mallocFloat stack 16)))
        (MemoryStack/stackPop)))

    (GL32/glDrawArrays GL32/GL_TRIANGLE_STRIP 0 4)))
