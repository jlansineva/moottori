(def lwjgl-natives ["linux" "macos" "windows"])

(def lwjgl-version "3.4.1")

(def lwjgl-modules ["lwjgl" "lwjgl-assimp" "lwjgl-glfw" "lwjgl-openal" "lwjgl-opengl" "lwjgl-stb"])

(defn lwjgl-modules-with-natives
  []
  (apply concat
         (let [lwjgl-package-prefix "org.lwjgl"]
           (for [module lwjgl-modules]
             (let [module-with-version [(symbol lwjgl-package-prefix module) lwjgl-version]]
               (into [module-with-version]
                     (for [platform lwjgl-natives]
                       (into module-with-version [:classifier (str "natives-" platform)]))))))))

(def dependencies
  (into '[[org.clojure/clojure "1.12.4"]
          [nrepl/nrepl "1.6.0"]]
        (lwjgl-modules-with-natives)))

(defproject conspiravision/pelinrakentaja-engine "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies ~dependencies
  :source-paths ["src" "src-common"]
;  :aot [pelinrakentaja-engine.core.desktop-launcher pelinrakentaja-engine.core.input]
  :jvm-opts ["--enable-native-access=ALL-UNNAMED"]
  :main pelinrakentaja-engine.core
  :target-path "target/%s"
  :profiles {:dev {:aot :all}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-XstartOnFirstThread"
                                  ]}})
