(ns pelinrakentaja-engine.core
  "Pelinrakentaja Moottori is a LibGDX wrapper for Clojure, part of Pelinrakentaja collection
  of tools. Pelinrakentaja is built to provide simple setup for games development in Clojure.
  The toolset is intended for small-form games where rapid development is key, i.e. game jams.

  Currently, Pelinrakentaja targets only desktop. Possible extensions to other platforms might
  be coming, but currently new platforms are not on the roadmap.

  Moottori provides necessary capabilities for handling different forms of input and output,
  but is agnostic on the game logic.

  Moottori is event-driven. Games using Moottori would subscribe to an input event-provider
  and dispatch entity-events."
  (:require [pelinrakentaja-engine.core.desktop-launcher :as launcher]
            #_[pelinrakentaja-engine.core.events :as events]
            [pelinrakentaja-engine.utils.log :as log]
        ;    [pelinrakentaja-engine.utils.keys :as keys]
            [pelinrakentaja-engine.utils.nrepl :as nrepl]
            [pelinrakentaja-engine.core.state :as state]
            [pelinrakentaja-engine.graphics.window :as window]
            [pelinrakentaja-engine.graphics.renderer :as renderer])
  (:gen-class))

;; TODO:
;; - add a logging macro, which takes a body in a do and includes log based on a log level!

(defn initialize-window
  [{:keys [repl? title entity-state]}]
  {:pre [(or (nil? entity-state)
             (instance? clojure.lang.Atom entity-state))]}
  (when entity-state
    (alter-var-root #'state/entity-state (constantly entity-state)))
  (when repl?
    (nrepl/launch-nrepl))
  (launcher/setup-window {:title title}))

(defmacro game-loop
  [& body]
  `(let [~'loop-fn (fn []
                   (do ~@body)
                   (System/exit 0))]
     (.start (Thread. ~'loop-fn))))

(defn run
  []
  (log/log :info :engine/run "Creating GLFW window")
  (window/create-window)
  (log/log :info :engine/run "Initializing GL renderer")
  (renderer/init-renderer)
  (while (not (window/should-window-close?))
    (renderer/render)
    (window/poll-events)
    (log/print-logs))

  (log/log :info :engine/run "Terminating window")
  (window/terminate-window)

  (log/print-logs))

;; needs to register entities
;; needs to update entities

                                        ;(def listen events/listener)
                                        ;(def dispatch events/dispatch)

                                        ;(def register-listener events/register-listener)
                                        ;(def clear-listener events/clear-listener)

(defn update!
  "Calling update somewhere in your logic updates the engine event queue and state. This is required."
  []
                                        ; (events/update-queue)
  (log/print-logs)
  (Thread/sleep 16))

(def entity-state state/entity-state)

;(def keymap keys/keymap)
;(def keyval keys/keyval)
