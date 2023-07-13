(ns pelinrakentaja-engine.core.graphics.sprites
  (:import [com.badlogic.gdx Gdx]
           [com.badlogic.gdx.graphics.g2d Sprite]))

(defn create-sprite
  [texture]
  (Sprite. texture))
