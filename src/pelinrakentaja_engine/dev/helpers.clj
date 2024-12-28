(ns pelinrakentaja-engine.dev.helpers
  (:require [pelinrakentaja-engine.core.events :as events]))

(defn activate-debug-mode
  []
  (events/dispatch [:debug/set-debug-mode true]))

(defn deactivate-debug-mode
  []
  (events/dispatch [:debug/set-debug-mode false]))
