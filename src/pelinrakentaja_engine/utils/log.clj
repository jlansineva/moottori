(ns pelinrakentaja-engine.utils.log)

(def enabled-logs #{:all
                    :input-adapter/key-down
                    :input-adapter/key-up
                    :event-handlers/key-down
                    :event-handlers/key-up
                    :engine/lifecycle})
(def enabled-log-levels #{:all :important :debug :info})

(def ignore-log-from #{:event/dispatch :game/update-items :add-entity})

(def log-queue (atom []))

(defn print-logs
  []
  (when-not (empty? @log-queue)
    (swap! log-queue
           #(do
              (mapv prn %)
              []))))

(defn log
  [log-level logger & params]
  (when (and
         (not (logger ignore-log-from))
         (or (logger enabled-logs)
             (:all enabled-logs))
         (or (log-level enabled-log-levels)
             (:all enabled-log-levels)))
    (swap! log-queue conj (apply str logger params))))
