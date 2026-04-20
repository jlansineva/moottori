(ns pelinrakentaja-engine.utils.log
  (:require [clojure.string :as str]))

(def logging (atom {:log-level :info}))

(def enabled-logs #{:all})
(def enabled-log-levels #{:all :important :debug :info})

(def ignore-log-from #{:event/dispatch :game/update-items :add-entity})

(def log-queue (atom []))

(defn set-log-level!
  [log-level] ;; TODO
  (if (some? (get enabled-log-levels log-level))
    (swap! logging assoc :log-level log-level)
    (println "error"))
  )

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
  (when (and
          (not (logger ignore-log-from))
          (not (:all ignore-log-from))
         (or (logger enabled-logs)
             (:all enabled-logs))
         (or (log-level enabled-log-levels)
             (:all enabled-log-levels)))
    (swap! log-queue conj (str logger ": " (str/join " : " params)))))
