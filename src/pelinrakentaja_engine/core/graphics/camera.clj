(ns pelinrakentaja-engine.core.graphics.camera
  (:require [pelinrakentaja-engine.utils.interop :as utils.interop])
  (:import [com.badlogic.gdx Gdx ApplicationAdapter]
           [com.badlogic.gdx.graphics GL20 OrthographicCamera PerspectiveCamera]
           [com.badlogic.gdx.utils.viewport ExtendViewport]
           [com.badlogic.gdx.graphics.g2d BitmapFont SpriteBatch]))

(defn create-camera
  [camera-mode]
  {:pre [(keyword? camera-mode)
         (some? (get #{:orthographic :perspective} camera-mode))]}
  (let [camera-instance (if (= camera-mode :orthographic)
                          (OrthographicCamera.)
                          (PerspectiveCamera.))]
    {:camera camera-instance
     :orthographic? (= :orthographic camera-mode)
     :perspective? (= :orthographic camera-mode)
     }))

(defn move-camera
  ([{:keys [camera] :as camera-data} dx dy]
   {:pre [(true? (:orthographic? camera-data))]}
   (.translate camera dx dy))

  ([{:keys [camera] :as camera-data} dx dy dz]
   {:pre [(true? (:perspective? camera-data))]}
   (.translate camera dx dy dz)))

(defn- -move-to-pos-ortho
  [{:keys [camera] :as camera-data} px py]
  (let [position (.-position camera)
        {:keys [x y]} (utils.interop/unwrap-vector2 position)
        dx (- px x)
        dy (- py y)]
    (move-camera camera-data dx dy)))

(defn- -move-to-pos-pers
  [{:keys [camera] :as camera-data} px py pz]
  (let [position (.-position camera)
        {:keys [x y z]} (utils.interop/unwrap-vector3 position)
        dx (- px x)
        dy (- py y)
        dz (- pz z)]
    (move-camera camera-data dx dy dz)))

(defn move-camera-to-position
  [camera-data px py pz]
  (let [{:keys [orthographic?]} camera-data]
    (if orthographic?
      (-move-to-pos-ortho camera-data px py)
      (-move-to-pos-pers camera-data px py pz))))
