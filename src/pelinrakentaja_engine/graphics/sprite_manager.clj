(ns pelinrakentaja-engine.graphics.sprite-manager
  (:require [pelinrakentaja-engine.graphics.sprite :as sprite]
            [pelinrakentaja-engine.utils.log :as log])
  (:import [org.joml Matrix4f]))

(def sprites (atom []))

;; TODO temporary, refine this
(defn add-sprite
  [context {:keys [x y z]}]
  (swap! sprites conj (merge {:model-matrix (.translate (Matrix4f.) x y z)}
                             context)))

(defn initialize
  []
  (log/log log/default-log-level :engine/sprite-manager "Initializing sprite-core")
  (sprite/initialize-sprite-core)
  (let [shader-and-uniforms (sprite/retrieve-shader ::sprite/default)
        transform {:x 0.0 :y 0.0 :z 0.0}
        transform2 {:x 0.5 :y 0.5 :z 0.0}]
    (add-sprite shader-and-uniforms transform)
    (add-sprite shader-and-uniforms transform2)))

(defn render []
  (sprite/render-sprite @sprites))
