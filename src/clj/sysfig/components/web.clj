(ns sysfig.components.web
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [port server handler]
  component/Lifecycle
  (start [component]
    (println "Starting web")
    (println (:handler handler))
    (let [server (run-jetty (:handler handler) {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (println "Stopping web")
    (when server
      (.stop server)
      component)))

(defn new-web-server
  [port]
  (map->WebServer {:port port}))
