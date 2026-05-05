(ns pelinrakentaja-engine.input.handler
  (:import [org.lwjgl.glfw GLFW]))

(def input-state (atom {:active-keys []
                        GLFW/GLFW_KEY_ESCAPE {:key-pressed? false
                                              :key-released? false
                                              :key-just-pressed? false
                                              :key-just-released? false
                                              :handler {:handler-fn (fn [context])
                                                        :context {:window nil}}
                                              }}))

;; TODO add safety checks so it's not as easy to overwrite keys
(defn setup-handler
  [key handler-fn & {:as context}]
  (swap! input-state update key assoc :handler {:handler-fn handler-fn :context context}))

(defn invoke-handler
  [key]
  (let [key-context (:handler (get @input-state key))
        {:keys [handler-fn context]} key-context]
    (handler-fn context)))

(defn is-key-pressed?
  [])

(defn is-key-just-pressed? [])

(defn is-key-released? [])

(defn is-key-just-released? [])
