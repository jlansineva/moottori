(ns pelinrakentaja-engine.utils.log)

(def enabled-logs #{:all
                    :input-adapter/key-down
                    :input-adapter/key-up
                    :event-handlers/key-down
                    :event-handlers/key-up
                    :engine/lifecycle})
(def enabled-log-levels #{:all :important :debug :info})

(defn log
  [log-level logger & params]
  (when (and
          (or (logger enabled-logs)
              (:all enabled-logs))
          (or (log-level enabled-log-levels)
              (:all enabled-log-levels)))
    (apply prn logger params)))
