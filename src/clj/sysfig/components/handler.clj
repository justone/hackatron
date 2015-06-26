(ns sysfig.components.handler
  (:require [com.stuartsierra.component :as component]
            [sysfig.handler :refer [make-handler]]))

(defrecord Handler [handler]
  component/Lifecycle
  (start [component]
    (println "Starting handler")
    (assoc component :handler (make-handler {})))
  (stop [component]
    (println "Stopping handler")
    (dissoc component :handler)))

(defn new-handler
  []
  (map->Handler {}))
