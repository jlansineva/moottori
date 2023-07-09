(ns pelinrakentaja-engine.core.graphics.textures
  (:require [pelinrakentaja-engine.core.events :as events])
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics Texture]))

(defonce textures-for-type
  (atom {}))

(defn load-texture-from-resource
  [id path]
  (swap! textures-for-type
         assoc id (Texture. (.internal (. Gdx -files) path)))
  (events/dispatch [:resources/resource-loaded]))
