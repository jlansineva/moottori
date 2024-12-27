(ns pelinrakentaja-engine.core.debug-events
  "Events for debug purposes only")

;; TODO: hide behind config parameter

(defn set-debug-mode
  [state debug-state]
  (assoc-in state [:debug :active] debug-state))

(defn detach-camera
  [state])

(defn retach-camera
  [])
