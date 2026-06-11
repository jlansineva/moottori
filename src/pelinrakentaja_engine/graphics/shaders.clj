(ns pelinrakentaja-engine.graphics.shaders
  (:require [pelinrakentaja-engine.utils.log :as log]
            [pelinrakentaja-engine.utils.definitions :as definitions])
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack]
           [org.joml Matrix4f]))

(def shaders (atom {}))

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

(def vertex-shader-basic
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

(def fragment-shader-basic
  (str "#version 150 core\n"
       "\n"
       "in vec3 vertexColor;\n"
       "\n"
       "out vec4 fragColor;\n"
       "\n"
       "void main() {\n"
       "    fragColor = vec4(vertexColor, 1.0);\n"
       "}\n"))

(defn with-shader
  [shaders]
  (log/log log/default-log-level :engine/shaders "Creating shaders" shaders)
  (cond-> {}
    (some #{:vertex} shaders)
    (assoc :vertex (GL32/glCreateShader GL32/GL_VERTEX_SHADER))

    (some #{:fragment} shaders)
    (assoc :fragment (GL32/glCreateShader GL32/GL_FRAGMENT_SHADER))

    (some #{:program} shaders)
    (assoc :program (GL32/glCreateProgram))))

(defn source-and-compile
  [shader-id shader-glsl]
  (log/log log/default-log-level :engine/shaders "Sourcing and compiling shader" shader-id)
  (GL32/glShaderSource shader-id shader-glsl)
  (GL32/glCompileShader shader-id))

(defn attach-to-program
  [shader-id shader-program]
  (log/log log/default-log-level :engine/shaders "Attaching shader to program" shader-id shader-program)
  (GL32/glAttachShader shader-program shader-id))

(defn bind-fragdata-location
  [shader-program color-number out-name]
  (GL32/glBindFragDataLocation shader-program color-number out-name))

(defn link-program
  [shader-program]
  (GL32/glLinkProgram shader-program))

(defn create-shader [& {:keys [shaders glsl bindings]}]
#_  {:pre []}
  (let [{:keys [program] :as shaders-and-program}
        (with-shader shaders)]
    (doseq [s shaders]
      (when-not (= s :program)
        (let [shader-glsl (get glsl s)]
          (source-and-compile (get shaders-and-program s) shader-glsl)
          (attach-to-program (get shaders-and-program s) program))))
    (bind-fragdata-location program
                            (get-in bindings [:fragdata-location :color-number])
                            (get-in bindings [:fragdata-location :out-name]))
    (link-program program)

    shaders-and-program))

(defn- get-uniform-location
  [program location]   ;; TODO error check
  (GL32/glGetUniformLocation program location))

(defn get-uniform-locations
  [program & locations]
  (mapv (partial get-uniform-location program) locations))

(defn set-attribute-pointer-in-vao-for-location
  [program location]
  (let [attribute (GL32/glGetAttribLocation program location)]
    (GL32/glEnableVertexAttribArray attribute)
    (GL32/glVertexAttribPointer attribute 3 GL32/GL_FLOAT false (* 6 definitions/size-float) 0)))
