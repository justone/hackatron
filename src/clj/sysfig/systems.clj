(ns sysfig.systems
  (:require [system.core :refer [defsystem]]
            (system.components
             [repl-server :refer [new-repl-server]])
            (sysfig.components
             [handler :refer [new-handler]]
             [web :refer [new-web-server]])
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]))

(defn dev-system
  []
  (component/system-map
    :handler (new-handler)
    :web (component/using (new-web-server (Integer. (env :http-port))) [:handler])))

(defn prod-system
  []
  (component/system-map
    :handler (new-handler)
    :web (component/using (new-web-server (Integer. (env :http-port))) [:handler])
    :repl-server (new-repl-server (Integer. (env :repl-port)))))
