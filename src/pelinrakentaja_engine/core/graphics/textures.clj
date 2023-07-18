(ns pelinrakentaja-engine.core.graphics.textures
  (:require [pelinrakentaja-engine.core.events :as events])
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics Texture Texture$TextureWrap]
   #_        [com.badlogic.gdx.graphics.Texture TextureWrap]
           [com.badlogic.gdx.graphics.g2d TextureRegion]))

(defn load-texture-from-resource
  [id path]
  (let [texture (Texture. (.internal (. Gdx -files) path))
        _ (.setWrap texture Texture$TextureWrap/Repeat Texture$TextureWrap/Repeat)
        region (TextureRegion. texture)]
    (events/dispatch [:resources/resource-loaded :texture id region])))
