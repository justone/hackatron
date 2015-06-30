(ns hackatron.components.handler
  (:require [com.stuartsierra.component :as component]
            [hackatron.handler :refer [make-handler]]))

(defrecord Handler [notifier handler carmine]
  component/Lifecycle
  (start [component]
    (println "Starting handler")
    (assoc component :handler (make-handler {:notifier notifier :carmine carmine})))
  (stop [component]
    (println "Stopping handler")
    (dissoc component :handler)))

(defn new-handler
  []
  (map->Handler {}))
