(ns pelinrakentaja-engine.core.desktop-launcher
  (:require [pelinrakentaja-engine.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration]
           #_[org.lwjgl3.input Keyboard])
  (:gen-class))

(defn -main []
  (let [config (Lwjgl3ApplicationConfiguration.)]
    (Lwjgl3Application. (pelinrakentaja-engine.core.Game.) config))
  #_(Keyboard/enableRepeatEvents true))
