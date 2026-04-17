(ns pelinrakentaja-engine.graphics.window
  (:require [pelinrakentaja-engine.config :as config])
  (:import [org.lwjgl.glfw GLFW GLFWKeyCallback]))

(def window (atom nil))

;; TODO: move to its own namespace and setup some sort of input management
(def key-callback (proxy [GLFWKeyCallback] []
                    (invoke [window key scancode action mods]
                      (println "key call back" "-" GLFW/GLFW_KEY_ESCAPE)
                      (when (= key GLFW/GLFW_KEY_ESCAPE)
                        (println "set close")
                        (GLFW/glfwSetWindowShouldClose window true)
                        (println "did set")))))

(defn create-window
  []
  (when-not (GLFW/glfwInit)
    (throw (IllegalStateException. "Unable to initialize glfw")))

  (println "hints")
  (GLFW/glfwDefaultWindowHints)

  (println "hints visible")
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (println "hints resizable")
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (println "hints major v")
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR 3) ;; TODO do not hard code
  (println "hints minor")
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MINOR 2)
  (println "hints profile")
  (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_PROFILE GLFW/GLFW_OPENGL_CORE_PROFILE)
  (println "hints fw compat")
  (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_FORWARD_COMPAT GLFW/GLFW_TRUE)

  (println "creating window")
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
