(ns pelinrakentaja-engine.graphics.shaders
  (:import [org.lwjgl.opengl GL GL32]
           [org.lwjgl.system MemoryStack]
           [org.joml Matrix4f]))

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
  (cond-> {}
      (some #{:vertex} shaders)
      (assoc :vertex (GL32/glCreateShader GL32/GL_VERTEX_SHADER))

      (some #{:fragment} shaders)
      (assoc :fragment (GL32/glCreateShader GL32/GL_FRAGMENT_SHADER))

      (some #{:program} shaders)
      (assoc :program (GL32/glCreateProgram))))

(defn source-and-compile
  [shader-id shader-glsl]
  (GL32/glShaderSource shader-id shader-glsl)
  (GL32/glCompileShader shader-id))

(defn attach-to-program
  [shader-id shader-program]
  (GL32/glAttachShader shader-program shader-id))

(defn bind-fragdata-location
  [shader-program color-number out-name]
  (GL32/glBindFragDataLocation shader-program color-number out-name))

(defn link-program
  [shader-program]
  (GL32/glLinkProgram shader-program))

(defn create-shader [& {:keys [shaders glsl bindings]}]
  {:pre []}
  (let [{:keys [program] :as shaders-and-program}
        (with-shader shaders)]
    (doseq [s shaders]
      (let [shader-glsl (get glsl s)]
        (source-and-compile (get shaders-and-program s) shader-glsl)
        (attach-to-program (get shaders-and-program s) program)))
    (bind-fragdata-location program
                            (get-in bindings [:fragdata-location :color-number])
                            (get-in bindings [:fragdata-location :out-name]))
    (link-program program)

    shaders-and-program))
