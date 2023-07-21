(ns pelinrakentaja-engine.core.graphics.textures
  (:require [pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.utils.log :as log])
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics Texture Texture$TextureWrap]
           [com.badlogic.gdx.graphics.g2d TextureRegion]))

(def resource-loaded? (events/listener :resources/resource-loaded?))

(defn load-texture-from-resource
  [id path]
  (log/log :debug :id id :path path)
  (when-not (resource-loaded? :texture id)
    (let [texture (Texture. (.internal (. Gdx -files) path))
          _ (.setWrap texture Texture$TextureWrap/Repeat Texture$TextureWrap/Repeat)]
      (log/log :debug :texture :h (.getHeight texture) :w (.getWidth texture))
      (TextureRegion. texture))))
