(ns pelinrakentaja-engine.graphics.window
  (:require [pelinrakentaja-engine.config :as config])
  (:import [org.lwjgl.glfw GLFW]))

(def window (atom nil))

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



  (GLFW/glfwMakeContextCurrent @window)
  (GLFW/glfwSwapInterval 1)
  (GLFW/glfwShowWindow @window))

(defn terminate-window
  []
  (when @window
    (GLFW/glfwDestroyWindow @window))
  (GLFW/glfwTerminate))
