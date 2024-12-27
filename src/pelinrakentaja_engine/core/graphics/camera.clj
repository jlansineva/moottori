(ns pelinrakentaja-engine.core.graphics.camera
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

(defn move-camera [camera-data dx dy dz]
  (let [{:keys [camera orthographic?]} camera-data]
    (if orthographic?
      (.translate camera dx dy)
      (.translate camera dx dy dz))))

(defn legacy-init [camera viewport]
  (let [camera-instance (:camera camera)]
    (set! (.-x (.-position camera-instance)) (-> viewport
                                                 .getWorldWidth
                                                 (/ 2)
                                                 (- 2)))))
