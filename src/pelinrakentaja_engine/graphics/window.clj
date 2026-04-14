(ns pelinrakentaja-engine.graphics.window
  (:require [pelinrakentaja-engine.config :as config])
  (:import [org.lwjgl.glfw GLFW GLFWKeyCallback]))

(def window (atom nil))

(def key-callback (proxy [GLFWKeyCallback] []
                             (invoke [window key scancode action mods]
                               (println key)
                               (when (= key GLFW/GLFW_KEY_ESCAPE)
                                 (GLFW/glfwSetWindowShouldClose @window true)))))

(defn create-window
  []
  (when-not (GLFW/glfwInit)
    (throw (IllegalStateException. "Unable")))
  (GLFW/glfwDefaultWindowHints)
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (reset! window (GLFW/glfwCreateWindow (config/get-config :window-default-x)
                                        (config/get-config :window-default-y)
                                        "Hello"
                                        0 0))

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
