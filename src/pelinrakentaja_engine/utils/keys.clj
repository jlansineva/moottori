(ns pelinrakentaja-engine.utils.keys
  (:require [clojure.set :as set])
  (:import [com.badlogic.gdx Input$Keys]))

(def keymap {:left Input$Keys/LEFT
             :right Input$Keys/RIGHT
             :up Input$Keys/UP
             :down Input$Keys/DOWN})

(def keyval (set/map-invert keymap))
