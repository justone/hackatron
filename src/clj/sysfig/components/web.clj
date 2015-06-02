(ns sysfig.components.web
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]]))

(defrecord WebServer [port server handler]
  component/Lifecycle
  (start [component]
    (println "Starting web")
    (println (:handler handler))
    (let [server (run-server (:handler handler) {:port port})]
      (assoc component :server server)))
  (stop [component]
    (println "Stopping web")
    (when server
      (server)
      component)))

(defn new-web-server
  [port]
  (map->WebServer {:port port}))
