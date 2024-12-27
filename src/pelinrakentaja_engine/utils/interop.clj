(ns pelinrakentaja-engine.utils.interop)

(defn unwrap-vector2
  [vec2]
  {:x (.-x vec2)
   :y (.-y vec2)})

(defn unwrap-vector3
  [vec3]
  {:x (.-x vec3)
   :y (.-y vec3)
   :z (.-z vec3)})
