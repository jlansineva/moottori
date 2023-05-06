(ns pelinrakentaja-engine.utils.log)

(def enabled-logs #{:all
                    :input-adapter/key-down
                    :input-adapter/key-up})
(def enabled-log-levels #{:all :important :debug :info})

(defn log
  [logger log-level & params]
  (when (and
          (or (logger enabled-logs)
              (:all enabled-logs))
          (or (log-level enabled-log-levels)
              (:all enabled-log-levels)))
    (apply prn logger params)))
