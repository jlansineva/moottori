(ns pelinrakentaja-engine.utils.log
  (:require [clojure.string :as str]))

(def logging (atom {:log-level :info
                    :log-mode {:debug :queue
                               :important :immediate
                               :info :queue}}))

(def enabled-logs #{:all})
(def enabled-log-levels #{:all :important :debug :info})

(def ignore-log-from #{})

(def log-queue (atom []))

(def default-log-level :important)

(defn set-log-level!
  [log-level] ;; TODO this would let you adjust log-level hierachically eg you could have most important logs up to certain point. think different verbose levels.
  (if (some? (get enabled-log-levels log-level))
    (swap! logging assoc :log-level log-level)
    (println "error"))
  )

(defn set-log-mode-for-log-level!
  "Mostly for REPL usage"
  [log-level log-mode]
  {:pre [(some? (#{:queue :immediate} log-mode))]}
  (swap! logging assoc-in [:log-mode log-level] log-mode))

(defn print-logs
  []
  (when-not (empty? @log-queue)
    (swap! log-queue
           #(do
              (doseq [l %]
                ;; TODO save log file
                (println l))
              []))))

(defn log
  [log-level logger & params]
  (let [{:keys [log-mode]} @logging]
    (when (and
            (not (logger ignore-log-from))
            (not (:all ignore-log-from))
            (or (logger enabled-logs)
                (:all enabled-logs))
            (or (log-level enabled-log-levels)
                (:all enabled-log-levels)))
      (if (= (log-level log-mode) :immediate)
        (println (str log-level " : " logger ": " (str/join " : " params)))
        (swap! log-queue conj (str log-level " : " logger ": " (str/join " : " params)))))))
