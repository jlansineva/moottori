(ns pelinrakentaja-engine.utils.nrepl
  (:require [nrepl.server :as nrepl]))

(defonce nrepl-server (atom nil))


;;TODO setup some configs for this (usage? port?)
(defn launch-nrepl
  []
  (when (nil? @nrepl-server)
    (reset! nrepl-server
            (nrepl/start-server
              :port 9666))))
