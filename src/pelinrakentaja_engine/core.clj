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
            [pelinrakentaja-engine.core.events :as events]))

(defn initialize-window
  [title]
  (launcher/setup-window title))

;; needs to register entities
;; needs to update entities

(defmacro run-logic [body])

(def listen events/listener)
(def dispatch events/dispatch)

(def register-listener events/register-listener)
(def clear-listener events/clear-listener)
