(ns pelinrakentaja-engine.dev.helpers
  (:require [pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.graphics.renderer :as renderer]))

(defn activate-debug-mode
  []
  (events/dispatch [:debug/set-debug-mode true]))

(defn deactivate-debug-mode
  []
  (events/dispatch [:debug/set-debug-mode false]))

(defn render-x-frames
  [x]
  (doseq [i (take x (range))]
    (renderer/render)))
