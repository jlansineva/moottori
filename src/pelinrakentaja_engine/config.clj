(ns pelinrakentaja-engine.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

;; internal, from current path
(def internal-config (edn/read-string (slurp (io/resource "config-internal.edn"))))
(def override-config (edn/read-string (slurp (io/resource "config.edn"))))
(def final-config (let [_final (merge internal-config override-config)]
                    (-> _final
                        (assoc :window-height (:window-default-y _final))
                        (assoc :window-width (:window-default-x _final)))))

(def config (atom final-config))

(defn get-config
  [key]
  (get @config key))

(defn set-config
  [key val]
  (swap! config assoc key val))

(defn with-configs
  [& {:as configs}]
  (doseq [key (keys configs)]
    (set-config key (get configs key))))
