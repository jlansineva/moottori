(ns pelinrakentaja-engine.utils.keys
  (:require [clojure.set :as set])
  (:import [com.badlogic.gdx Input$Keys]))

(def keymap {:left Input$Keys/LEFT
             :right Input$Keys/RIGHT
             :up Input$Keys/UP
             :down Input$Keys/DOWN})

(def debug-keymap {:debug-up Input$Keys/I
                   :debug-left Input$Keys/J
                   :debug-down Input$Keys/K
                   :debug-right Input$Keys/L})

(def keyval (set/map-invert keymap))

(def debug-keyval (set/map-invert debug-keymap))
