(ns pelinrakentaja-engine.graphics.window
  (:require [pelinrakentaja-engine.config :as config]
            [pelinrakentaja-engine.utils.log :as log])
  (:import [org.lwjgl.glfw GLFW GLFWKeyCallback]))

(def window (atom nil))

;; TODO: move to its own namespace and setup some sort of input management
(def key-callback (proxy [GLFWKeyCallback] []
                    (invoke [window key scancode action mods]
                      (when (= key GLFW/GLFW_KEY_ESCAPE)
                        (GLFW/glfwSetWindowShouldClose window true)))))

(defn create-window
  []
  (log/log :info :graphics/window "create-window" "Initializing GLFW")
  (when-not (GLFW/glfwInit)
    (throw (IllegalStateException. "Unable to initialize glfw")))

  (log/log :info :graphics/window "create-window" "Setting default GLFW Window Hints")
  (GLFW/glfwDefaultWindowHints)

  (let [glfw-visible GLFW/GLFW_FALSE
        glfw-resizable GLFW/GLFW_TRUE
        glfw-context-major-version (config/get-config :glfw-context-major-version)
        glfw-context-minor-version (config/get-config :glfw-context-minor-version)
        glfw-opengl-profile GLFW/GLFW_OPENGL_CORE_PROFILE
        glfw-opengl-forward-compat GLFW/GLFW_TRUE]
    (log/log :info :graphics/window "create-window" "Window visible" glfw-visible)
    (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE glfw-visible)

    (log/log :info :graphics/window "create-window" "Window resizable" glfw-resizable)
    (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE glfw-resizable)

    (log/log :info :graphics/window "create-window" "GLFW Context Version Major" glfw-context-major-version)
    (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR glfw-context-major-version)

    (log/log :info :graphics/window "create-window" "GLFW Context Version Minor" glfw-context-minor-version)
    (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MINOR glfw-context-minor-version)

    (log/log :info :graphics/window "create-window" "GLFW OpenGL profile" glfw-opengl-profile)
    (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_PROFILE glfw-opengl-profile)

    (log/log :info :graphics/window "create-window" "GLFW OpenGL Forward Compat" glfw-opengl-forward-compat)
    (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_FORWARD_COMPAT glfw-opengl-forward-compat))

  (log/log :info :graphics/window "create-window" "Creating window")
  (reset! window (GLFW/glfwCreateWindow (config/get-config :window-width)
                                        (config/get-config :window-height)
                                        "Hello"
                                        0 0))

  (when (nil? @window)
    (throw (Exception. "WIndow is nil")))

  (GLFW/glfwSetKeyCallback @window
                           key-callback)

  (GLFW/glfwMakeContextCurrent @window)
  (GLFW/glfwSwapInterval 1)
  (GLFW/glfwShowWindow @window))

(defn terminate-window
  []
  (when @window
    (GLFW/glfwDestroyWindow @window))
  (GLFW/glfwTerminate))

(defn swap-buffers
  []
  (GLFW/glfwSwapBuffers @window))

(defn should-window-close?
  []
  (GLFW/glfwWindowShouldClose @window))

(defn poll-events
  []
  (GLFW/glfwPollEvents))
