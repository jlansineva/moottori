(ns pelinrakentaja-engine.core.input
  (:require [pelinrakentaja-engine.core.state :as state])
  (:import [com.badlogic.gdx InputAdapter]))

(gen-class
  :name pelinrakentaja-engine.core.input.Input
  :extends com.badlogic.gdx.InputAdapter)

(defn -keyDown
  [^InputAdapter this key-code]
  (prn :> this)
  (prn :> key-code)
  (swap! state/renderable-entities (fn [entities]
                                     (prn :> entities)
                                     (into {} (mapv (fn [[id entity]]
                                                      (prn :> id entity)
                                                      [id (update entity :x #(float (inc %)))]) entities))))
  true)
