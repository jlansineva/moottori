(ns pelinrakentaja-engine.dev.dev
  (:require [pelinrakentaja-engine.graphics.window :as window]
            [pelinrakentaja-engine.graphics.renderer :as renderer]
            [pelinrakentaja-engine.utils.log :as log]))

(defn test-loop
  []
  )

;; TODO this is blocking, so only for testing purposes. need to implement some loop approach
(defn run-quad-test
  []
  (log/log :info :engine/run "Creating GLFW window")
  (window/create-window)
  (log/log :info :engine/run "Initializing GL renderer")
  (renderer/init-renderer)
  (while (not (window/should-window-close?))
    (renderer/render)
    (window/poll-events)
    (log/print-logs))

  (log/log :info :engine/run "Terminating window")
  (window/terminate-window)

  (log/print-logs))
