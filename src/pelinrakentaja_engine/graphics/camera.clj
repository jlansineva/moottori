(ns pelinrakentaja-engine.graphics.camera
  (:require [clojure.math :as math]
            [pelinrakentaja-engine.input.handler :as handler])
  (:import [org.joml Matrix4f Vector3f]
           [org.lwjgl.glfw GLFW GLFWKeyCallback]))

(def camera (atom {:transform (doto (Matrix4f.)
                                    (.lookAt 0.0 0.0 5.0
                                             0.0 0.0 0.0
                                             0.0 1.0 0.0))
                   :projection (doto (Matrix4f.)
                                 (.perspective
                                   (math/to-radians 70.0)
                                   1.0
                                   0.01
                                   100.0)
                                 #_(.lookAt 0.0 10.0 5.0
                                          0.0 0.0 0.0
                                          0.0 1.0 0.0))}))


(defn -set-camera-position
  [transform x y z]
  (.setTranslation transform x y z))

(defn set-camera-position
  [& {:keys [x y z]}]
  (swap! camera update :transform -set-camera-position x y z))

(defn initialize-camera
  [& {:keys [x y z]
      :or   {x 0.0
             y 10.0
             z 5.0}}]
  #_(set-camera-position :x x :y y :z z)
  (handler/setup-handlers GLFW/GLFW_KEY_LEFT
                          :on-held-fn (fn [_context]
                                        (swap! camera update :transform (fn [transform]
                                                                          (.translate transform -0.2 0.0 0.0)))))
  (handler/setup-handlers GLFW/GLFW_KEY_RIGHT
                          :on-held-fn (fn [_context]
                                        (swap! camera update :transform (fn [transform]
                                                                          (.translate transform 0.2 0.0 0.0)))))

  (handler/setup-handlers GLFW/GLFW_KEY_UP
                          :on-held-fn (fn [_context]
                                        (swap! camera update :transform (fn [transform]
                                                                          (.translate transform 0.0 -0.2 0.0)))))

  (handler/setup-handlers GLFW/GLFW_KEY_DOWN
                          :on-held-fn (fn [_context]
                                        (swap! camera update :transform (fn [transform]
                                                                          (.translate transform 0.0 0.2 0.0))))))

(defn update-camera
  []
  )
