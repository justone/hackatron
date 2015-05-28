(defproject sysfig "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript  "0.0-3211"]
                 [ring "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [compojure "1.2.0"]
                 [hiccup "1.0.5"]
                 [org.danielsz/system "0.1.1"]
                 [environ "1.0.0"]
                 [org.clojure/tools.nrepl "0.2.5"]]
  :plugins [[lein-environ "1.0.0"]
            [lein-cljsbuild  "1.0.5"]
            [lein-figwheel  "0.3.3"]]
  :clean-targets ^{:protect false} [:target-path "resources/public/cljs" "resources/public/cljs/out-prod"]
  :profiles {:dev {:source-paths ["dev"]
                   :env {:http-port 3000}}
             :prod {:env {:http-port 8000
                          :repl-port 8001}
                    ; unfortunately, deps here don't work
                    ; https://github.com/technomancy/leiningen/issues/1763
                    :dependencies [[org.clojure/tools.nrepl "0.2.5"]]}
             :uberjar {:aot :all}}
  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {
                                   :websocket-host "silo"
                                   }
                        :compiler {:main sysfig.core
                                   :asset-path "cljs/out"
                                   :output-to  "resources/public/cljs/main.js"
                                   :output-dir "resources/public/cljs/out"
                                   :source-map true}
                        }
                       {:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:main sysfig.core
                                   :cache-analysis true
                                   :optimizations :advanced
                                   :output-to  "resources/public/cljs/main.js"
                                   :output-dir "resources/public/cljs/out-prod"
                                   :source-map  "resources/public/cljs/main.js.map"}
                        }]
              }
  :figwheel {
             :css-dirs ["resources/public/css"]
             }
  :main ^:skip-aot sysfig.core
  :target-path "target/%s")
