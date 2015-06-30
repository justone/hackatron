(ns hackatron.systems
  (:require [system.core :refer [defsystem]]
            (system.components
             [repl-server :refer [new-repl-server]])
            (hackatron.components
             [notifier :refer [new-notifier]]
             [handler :refer [new-handler]]
             [carmine :refer [new-carmine]]
             [web :refer [new-web-server]])
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]))

(defn dev-system
  []
  (component/system-map
    :notifier (new-notifier (env :notification-address))
    :handler (component/using (new-handler) [:notifier :carmine])
    :carmine (new-carmine)
    :web (component/using (new-web-server (Integer. (env :http-port))) [:handler])))

(defn prod-system
  []
  (component/system-map
    :notifier (new-notifier (env :notification-address))
    :handler (component/using (new-handler) [:notifier])
    :web (component/using (new-web-server (Integer. (env :http-port))) [:handler])
    :repl-server (new-repl-server (Integer. (env :repl-port)))))
