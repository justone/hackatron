(ns hackatron.components.handler
  (:require [com.stuartsierra.component :as component]
            [hackatron.handler :refer [make-handler]]))

(defrecord Handler [notifier handler data]
  component/Lifecycle
  (start [component]
    (println "Starting handler")
    (let [services {:notifier (:handler notifier) :data (:data data)}
          handler (make-handler services)]
      (assoc component :handler handler)))
  (stop [component]
    (println "Stopping handler")
    (dissoc component :handler)))

(defn new-handler
  []
  (map->Handler {}))
