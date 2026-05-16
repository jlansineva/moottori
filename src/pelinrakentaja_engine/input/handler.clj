(ns pelinrakentaja-engine.input.handler
  (:import [org.lwjgl.glfw GLFW GLFWKeyCallback]))

(def input-state (atom {:active-keys {}
                        GLFW/GLFW_KEY_ESCAPE {:key-pressed? false
                                              :key-released? false
                                              :key-just-pressed? false
                                              :key-just-released? false
                                              :handler {:handler-fn (fn [context])
                                                        :key-press-handler-fn (fn [context])
                                                        :key-release-handler-fn (fn [context])
                                                        :context {:window nil}}
                                              }}))



;; TODO add safety checks so it's not as easy to overwrite keys
(defn setup-handler
  [key handler-fn & {:as context}]
  (swap! input-state update key assoc :handler {:handler-fn handler-fn}))

(defn setup-press-handler
  [key handler-fn & {:as context}]
  (swap! input-state update key assoc :handler {:key-press-handler-fn handler-fn}))

(defn setup-release-handler
  [key handler-fn & {:as context}]
  (swap! input-state update key assoc :handler {:key-release-handler-fn handler-fn}))

(defn setup-handlers
  [key & {:keys [on-held-fn on-press-fn on-release-fn] :as context}]
  (let [context (dissoc context :handler-fn :on-press-fn :on-release-fn)]
    (when on-held-fn (setup-handler key on-held-fn context))
    (when on-press-fn (setup-press-handler key on-press-fn context))
    (when on-release-fn (setup-release-handler key on-release-fn context))))

;; TODO when-let
(defn invoke-handler
  [key handler-type]
  (when-let [key-context (:handler (get @input-state key))]
    (when-let [handler-fn (get key-context handler-type)]
      (handler-fn (:context key-context)))))

(defn is-key-pressed?
  [])

(defn is-key-just-pressed? [])

(defn is-key-released? [])

(defn is-key-just-released? [])

(defn update-inputs
  []
  (let [{:keys [active-keys] :as state} @input-state
        pressed-keys (keys active-keys)]
    (doseq [k pressed-keys]
      (invoke-handler k :handler-fn))))

(defn update-key-pressed-state
  [state key]
  (-> state
      (update :active-keys assoc key true)
      (assoc-in [key :key-pressed?] true)
      (assoc-in [key :key-just-pressed?] true)
      (assoc-in [key :key-released?] false)
      (assoc-in [key :key-just-released?] false)))

(defn update-key-released-state
  [state key]
  (-> state
      (update :active-keys dissoc key)
      (assoc-in [key :key-pressed?] false)
      (assoc-in [key :key-just-pressed?] false)
      (assoc-in [key :key-released?] true)
      (assoc-in [key :key-just-released?] true)))

(defn- key-press-action
  [key]
  (swap! input-state update-key-pressed-state key)
  (invoke-handler key :key-press-handler-fn))

(defn- key-release-action
  [key]
  (swap! input-state update-key-released-state key)
  (invoke-handler key :key-release-handler-fn))

(def key-callback (proxy [GLFWKeyCallback] []
                    (invoke [window key scancode action mods]
                      (println "?==========" action GLFW/GLFW_PRESS GLFW/GLFW_RELEASE)
                      (cond
                        (= action GLFW/GLFW_PRESS) (key-press-action key)
                        (= action GLFW/GLFW_RELEASE) (key-release-action key)))))
