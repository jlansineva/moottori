(ns pelinrakentaja-engine.graphics.camera
  (:require [clojure.math :as math])
  (:import [org.joml Matrix4f]))

(def camera (atom {:transform (doto (Matrix4f.)
                                    (.lookAt 0.0 10.0 5.0
                                             0.0 0.0 0.0
                                             0.0 1.0 0.0))
                   :projection (.perspective (Matrix4f.)
                                               (math/to-radians 60.0)
                                               1.0
                                               0.01
                                               100.0)}))
