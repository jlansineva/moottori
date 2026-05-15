(ns pelinrakentaja-engine.graphics.sprite
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.graphics.camera :as camera])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack]
           [org.joml Matrix4f]))

(def data (atom {:vertex-buffer-object nil
                 :vertex-array-object nil
                 :model-uniform-location nil
                 :view-uniform-location nil
                 :projection-uniform-location nil}))

(def shaders (atom {}))

(def vertex-shader
  (str "#version 150 core\n"
       "in vec3 position;\n"
       "\n"
       "in vec3 color;\n"
       "uniform mat4 model;\n"
       "uniform mat4 view;\n"
       "uniform mat4 projection;\n"
       "\n"
       "out vec3 vertexColor;\n"
       "\n"
       "void main() {\n"
       "    vertexColor = color;\n"
       "    gl_Position = projection * view * model * vec4(position, 1.0);\n"
       "}\n"))

(def fragment-shader
  (str "#version 150 core\n"
       "\n"
       "in vec3 vertexColor;\n"
       "\n"
       "out vec4 fragColor;\n"
       "\n"
       "void main() {\n"
       "    fragColor = vec4(vertexColor, 1.0);\n"
       "}\n"))

(def sprite-vertices [0.0 1.0 -1.0 1.0 0.0 0.0
                      0.0 0.0 -1.0 0.0 1.0 0.0
                      1.0 1.0 -1.0 0.0 0.0 1.0
                      1.0 0.0 -1.0 1.0 1.0 0.0])

(def sprite-quad-vertices (float-array sprite-vertices))

(defn retrieve-shader
  [shader-key]
  (get @shaders shader-key))

(defn store-shader
  [shader-key
   & {:keys [shader-program-id vertex-shader-id fragment-shader-id
             model-uniform-location view-uniform-location projection-uniform-location]}]
  (when-not (get @shaders shader-key)
    (let [program-map {:shader-program-id           shader-program-id
                       :vertex-shader-id            vertex-shader-id
                       :fragment-shader-id          fragment-shader-id
                       :model-uniform-location      model-uniform-location
                       :view-uniform-location       view-uniform-location
                       :projection-uniform-location projection-uniform-location}]
      (swap! shaders assoc shader-key program-map))))

(defn initialize-vbo
  []
  (let [vertices (float-array sprite-vertices)
        stack (MemoryStack/stackPush)
        vertices-buffer (.mallocFloat stack (count vertices))
        vertex-buffer-object-id (GL32/glGenBuffers)
        ;; TODO just gl_array_buffer?
        ]
    (doto vertices-buffer
      (.put vertices)
      (.flip))
    (GL32/glBindBuffer GL32/GL_ARRAY_BUFFER vertex-buffer-object-id)
    (GL32/glBufferData GL32/GL_ARRAY_BUFFER vertices GL32/GL_STATIC_DRAW)
    (swap! data assoc :vertex-buffer-object vertex-buffer-object-id)
    (MemoryStack/stackPop))
  ;; TODO: add possibility to generate VBOs for more different quads.
  )

(defn initialize-vao
  []
  (let [vertex-array-object-id (GL32/glGenVertexArrays)]
    (log/log :info :graphics/sprite "Generating VAO" "errors" )
    (GL32/glBindVertexArray vertex-array-object-id)
    (swap! data assoc :vertex-array-object vertex-array-object-id)))

(defn compile-shader
  [& {:keys [shader-id shader shader-program]}]
  (GL32/glShaderSource shader-id shader)
  (GL32/glCompileShader shader-id)
  (GL32/glAttachShader shader-program shader-id))

(defn compile-shaders
  []
  (let [vertex-shader-id   (GL32/glCreateShader GL32/GL_VERTEX_SHADER)
        fragment-shader-id (GL32/glCreateShader GL32/GL_FRAGMENT_SHADER)
        shader-program     (GL32/glCreateProgram)]

    (compile-shader :shader-id vertex-shader-id :shader vertex-shader :shader-progam shader-program)
    (compile-shader :shader-id fragment-shader-id :shader fragment-shader :shader-progam shader-program)

    (GL32/glBindFragDataLocation shader-program 0 "fragColor")
    (GL32/glLinkProgram shader-program)

    (let [model-location      (GL32/glGetUniformLocation shader-program "model")
          view-location       (GL32/glGetUniformLocation shader-program "view")
          projection-location (GL32/glGetUniformLocation shader-program "projection")

          _          (store-shader ::default
                                   :shader-program-id shader-program
                                   :fragment-shader-id fragment-shader-id
                                   :vertex-shader-id vertex-shader-id
                                   :model-uniform-location model-location
                                   :view-uniform-location view-location
                                   :projection-uniform-location projection-location)
          float-size 4

          pos-attrib (GL32/glGetAttribLocation shader-program "position")
          _          (GL32/glEnableVertexAttribArray pos-attrib)
          _          (GL32/glVertexAttribPointer pos-attrib 3 GL32/GL_FLOAT false (* 6 float-size) 0)

          col-attrib (GL32/glGetAttribLocation shader-program "color")
          _          (GL32/glEnableVertexAttribArray col-attrib)
          _          (GL32/glVertexAttribPointer col-attrib 3 GL32/GL_FLOAT false (* 6 float-size) (* 3 float-size))]))

;; TODO: initializes just a single quad type. It might be useful to provide options for a set of quads in a single VBO
  (defn initialize-sprite-core
    []
    (initialize-vao)
    (initialize-vbo)
    (compile-shaders)))

(defn render-sprite
  [context]
  (let [{:keys [vertex-array-object
                vertex-buffer-object]} @data]
    (GL32/glBindVertexArray vertex-array-object)
    (GL32/glBindBuffer GL32/GL_ARRAY_BUFFER vertex-buffer-object)

    (doseq [s context]
      (let [{:keys [shader-program-id
                    model-uniform-location
                    view-uniform-location
                    projection-uniform-location
                    model-matrix]} s]
        (GL32/glUseProgram shader-program-id)

        (let [stack (MemoryStack/stackPush)]
          ;; TODO: add camera API functions
          (GL32/glUniformMatrix4fv model-uniform-location false (.get model-matrix (.mallocFloat stack 16)))
          (GL32/glUniformMatrix4fv view-uniform-location false (.get (:transform @camera/camera) (.mallocFloat stack 16)))
          (GL32/glUniformMatrix4fv projection-uniform-location false (.get (:projection @camera/camera) (.mallocFloat stack 16)))
          (MemoryStack/stackPop)))

      (GL32/glDrawArrays GL32/GL_TRIANGLE_STRIP 0 4))))
