(ns pelinrakentaja-engine.dev.debug-mode
  "Various tools for debugging and live development of the game"
  (:require [pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.utils.keys :as utils.keys]
            [pelinrakentaja-engine.core.graphics.camera :as graphics.camera]))

(def pressed-keys (events/listener :input/pressed-keys))

(defn debug-mode-update
  [camera]
  (let [keys (pressed-keys)
        debug-camera-movement (cond-> {:x 0 :y 0}
                                (get-in keys [(:debug-up utils.keys/debug-keymap) :pressed?])
                                (update :y inc)

                                (get-in keys [(:debug-down utils.keys/debug-keymap) :pressed?])
                                (update :y dec)

                                (get-in keys [(:debug-left utils.keys/debug-keymap) :pressed?])
                                (update :x dec)

                                (get-in keys [(:debug-right utils.keys/debug-keymap) :pressed?])
                                (update :x inc))]
    (graphics.camera/move-camera camera
                                 (:x debug-camera-movement)
                                 (:y debug-camera-movement))))
