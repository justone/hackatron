(defproject hackatron "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :dependencies [
                 ;; clj
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [ring "1.4.0"]
                 [http-kit "2.1.19"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [org.danielsz/system "0.1.9"]
                 [environ "1.0.0"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [io.forward/sendgrid-clj "1.0"]
                 [com.taoensso/sente "1.6.0"]
                 [com.taoensso/carmine "2.11.1"]
                 [com.cognitect/transit-clj "0.8.281"]

                 ;; cljs
                 [com.cognitect/transit-cljs "0.8.220"]
                 [org.omcljs/om "0.8.8"]
                 [racehub/om-bootstrap "0.5.3"]
                 [prismatic/om-tools "0.3.12"]]
  :plugins [[lein-environ "1.0.0"]
            [lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.7"]]
  :clean-targets ^{:protect false} [:target-path "resources/public/cljs" "resources/public/cljs/out-prod"]
  :profiles {:dev {:source-paths ["dev"]
                   :env {:http-port 3000}}
             :prod {:env {:http-port 8000
                          :repl-port 8001}
                    ; unfortunately, deps here don't work
                    ; https://github.com/technomancy/leiningen/issues/1763
                    :dependencies [[org.clojure/tools.nrepl "0.2.10"]]}
             :uberjar {:aot :all}}
  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {
                                   :websocket-host ~(or (System/getenv "FIG_HOST") "localhost")
                                   :on-jsload "hackatron.core/show-gui!"
                                   }
                        :compiler {:main hackatron.core
                                   :asset-path "cljs/out"
                                   :output-to  "resources/public/cljs/main.js"
                                   :output-dir "resources/public/cljs/out"
                                   :source-map true}
                        }
                       {:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:main hackatron.core
                                   :cache-analysis true
                                   :optimizations :advanced
                                   :output-to  "resources/public/cljs/main.js"
                                   :output-dir "resources/public/cljs/out-prod"
                                   :source-map  "resources/public/cljs/main.js.map"}
                        }]
              }
  :figwheel {
             :css-dirs ["resources/public/css"]
             :server-port 13449
             :nrepl-port 7888
             }
  :main ^:skip-aot hackatron.core
  :target-path "target/%s")
