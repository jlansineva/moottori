(ns pelinrakentaja-engine.dev.dev
  (:require
   [pelinrakentaja-engine.core :as core]
   [pelinrakentaja-engine.graphics.window :as window]
   [pelinrakentaja-engine.graphics.renderer :as renderer]
   [pelinrakentaja-engine.utils.log :as log]))

(defn test-loop
  []
  )

;; TODO this is blocking, so only for testing purposes. need to implement some loop approach
(defn run-quad-test
  []
  (prn :a)
  (core/initialize)
  (prn :b)
  (while (not (window/should-window-close?))
    (renderer/render)
    (window/poll-events)
    (core/update!)
    (log/print-logs))

  (log/log :info :engine/run "Terminating window")
  (window/terminate-window)

  (log/print-logs))
