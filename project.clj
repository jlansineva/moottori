(defproject conspiravision/pelinrakentaja-engine "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [nrepl/nrepl "1.1.0-alpha1"]
                 [com.badlogicgames.gdx/gdx "1.11.0"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl3 "1.11.0"]
                 [com.badlogicgames.gdx/gdx-box2d "1.11.0"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.11.0"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.11.0"
                  :classifier "natives-desktop"]]
  :source-paths ["src" "src-common"]
  :aot [pelinrakentaja-engine.core.desktop-launcher pelinrakentaja-engine.core.input]
  :main pelinrakentaja-engine.core.desktop-launcher
  :target-path "target/%s"
  :profiles {:dev {:aot :all
                       :jvm-opts ["-XstartOnFirstThread"]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-XstartOnFirstThread"]}})
