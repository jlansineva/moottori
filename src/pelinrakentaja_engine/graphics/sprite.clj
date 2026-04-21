(ns pelinrakentaja-engine.graphics.sprite
  (:require [pelinrakentaja-engine.utils.log :as log])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack]))

(def data (atom {:vertex-buffer-object nil
                 :vertex-array-object nil
                 :fragment-shader-id nil
                 :vertex-shader-id nil
                 :shader-program-id nil}))

(def vertex-shader
  (str "#version 150 core\n"
       "in vec3 position;\n"
       "\n"
       "in vec3 color;\n"
       "\n"
       "out vec3 vertexColor;\n"
       "\n"
       "void main() {\n"
       "    vertexColor = color;\n"
       "    mat4x4 mvp = mat4x4(0.0);\n"
       "    mvp[0] = vec4(1.0, 0.0, 0.0, 0.0);\n"
       "    mvp[1] = vec4(0.0, 1.0, 0.0, 0.0);\n"
       "    mvp[2] = vec4(0.0, 0.0, 1.0, 0.0);\n"
       "    mvp[3] = vec4(0.0, 0.0, 0.0, 1.0);\n"
       "    gl_Position = mvp * vec4(position, 1.0);\n"
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

(def sprite-quad-vertices (float-array [0.0 1.0 0.0 1.0 0.0 0.0
                                        0.0 0.0 0.0 0.0 1.0 0.0
                                        1.0 1.0 0.0 0.0 0.0 1.0
                                        1.0 0.0 0.0 1.0 1.0 0.0]))

(defn initialize-vbo
  []
  (let [stack (MemoryStack/stackPush)
        vertices-buffer (.mallocFloat stack (count sprite-quad-vertices))
        vertex-buffer-object-id (GL32/glGenBuffers)
        ;; TODO just gl_array_buffer?
 ]
    (doto vertices-buffer
            (.put sprite-quad-vertices)
            (.flip))
    (GL32/glBindBuffer GL32/GL_ARRAY_BUFFER vertex-buffer-object-id)
    (GL32/glBufferData GL32/GL_ARRAY_BUFFER sprite-quad-vertices GL32/GL_STATIC_DRAW)
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

(defn compile-shaders
  []
  (let [vertex-shader-id (GL32/glCreateShader GL32/GL_VERTEX_SHADER)
        fragment-shader-id (GL32/glCreateShader GL32/GL_FRAGMENT_SHADER)
        shader-program (GL32/glCreateProgram)]

    ;; TODO DRY
    (GL32/glShaderSource vertex-shader-id vertex-shader)
    (GL32/glCompileShader vertex-shader-id)

    (GL32/glShaderSource fragment-shader-id fragment-shader)
    (GL32/glCompileShader fragment-shader-id)

    (GL32/glAttachShader shader-program vertex-shader-id)
    (GL32/glAttachShader shader-program fragment-shader-id)

    (GL32/glBindFragDataLocation shader-program 0 "fragColor")
    (GL32/glLinkProgram shader-program)

    (swap! data assoc :fragment-shader-id fragment-shader-id
           :vertex-shader-id vertex-shader-id
           :shader-program-id shader-program)

    (let [float-size 4

          pos-attrib (GL32/glGetAttribLocation shader-program "position")
          _ (GL32/glEnableVertexAttribArray pos-attrib)
          _ (GL32/glVertexAttribPointer pos-attrib 3 GL32/GL_FLOAT false (* 6 float-size) 0)

          col-attrib (GL32/glGetAttribLocation shader-program "color")
          _ (GL32/glEnableVertexAttribArray col-attrib)
          _ (GL32/glVertexAttribPointer col-attrib 3 GL32/GL_FLOAT false (* 6 float-size) (* 3 float-size))])))

;; TODO: initializes just a single quad type. It might be useful to provide options for a set of quads in a single VBO
(defn initialize-sprite-core
  []
  (initialize-vao)
  (initialize-vbo)
  (compile-shaders))

(defn render-sprite
  []
  (let [{:keys [shader-program-id
                vertex-array-object
                vertex-buffer-object]} @data]
    (GL32/glUseProgram shader-program-id)
    (GL32/glBindVertexArray vertex-array-object)
    (GL32/glBindBuffer GL32/GL_ARRAY_BUFFER vertex-buffer-object)
    ;; TODO: allow for creating large arrays
    (GL32/glDrawArrays GL32/GL_TRIANGLE_STRIP 0 4)))
