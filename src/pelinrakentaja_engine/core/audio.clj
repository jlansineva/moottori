(ns pelinrakentaja-engine.core.audio
  (:require [pelinrakentaja-engine.core.events :as events]
             [pelinrakentaja-engine.utils.log :as log])
  (:import [com.badlogic.gdx Gdx]))

(def resource-loaded? (events/listener :resources/resource-loaded?))

(defn load-sfx-from-resources
  [id path]
  (when-not (resource-loaded? :sfx id)
    (let [audio (. Gdx -audio)]
      (.newSound audio (.internal (. Gdx -files) path)))))

(defn load-music-from-resources
  [id path]
  (when-not (resource-loaded? :music id)
    (let [audio (. Gdx -audio)]
      (.newMusic audio (.internal (. Gdx -files) path)))))

(defn play-music-with-id
  [state id]
  (log/log :debug :music id)
  (let [music (get-in state [:resources :music id])]
    (when (and (some? music)
               (not
                (.isPlaying music)))
      (.play music)))
  state)

(defn play-sfx-with-id
  [state id]
  (log/log :debug :sfx id)
  (let [music (get-in state [:resources :sfx id])]
    (when (and (some? music)
               (not
                (.isPlaying music)))
      (.play music)))
  state)
