(ns pelinrakentaja-engine.core.desktop-launcher
  (:require [pelinrakentaja-engine.core.game :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration]
           #_[org.lwjgl3.input Keyboard])
  (:gen-class))

(defn setup-window
  [{:keys [title width height]}]
  (let [config (Lwjgl3ApplicationConfiguration.)]
    (.setTitle config (or title "Game"))
    (.setWindowedMode config (or width 800) (or height 600))
    (Lwjgl3Application. (pelinrakentaja-engine.core.game.Game.) config)))

(defn -main []
  (setup-window {})
  #_(Keyboard/enableRepeatEvents true))
