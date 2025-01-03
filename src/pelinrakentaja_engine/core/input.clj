(ns pelinrakentaja-engine.core.input
  (:require [pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.utils.log :as log])
  (:import [com.badlogic.gdx InputAdapter]))

(defn create-input-adapter
  []
  (proxy [InputAdapter] []
    (keyDown [key-code]
      (log/log :debug :input-adapter/key-down key-code)
      (events/dispatch [:input/key-down key-code])
      true)
    (keyUp [key-code]
      (log/log :debug :input-adapter/key-up)
      (events/dispatch [:input/key-up key-code])
      true)
    (touchDown [x y pointer button] 
      (events/dispatch [:input/mouse-down button x y])
      true)
    (touchUp [x y pointer button]
      (events/dispatch [:input/mouse-up button x y])
      true)
    (mouseMoved [x y]
      (events/direct-state-access [:input/mouse-moved x y])
      true)))
