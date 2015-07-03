(ns hackatron.components.notifier
  (:require [com.stuartsierra.component :as component]
            [hackatron.notifier :refer [send-email]]))

(defrecord Notifier [auth handler]
  component/Lifecycle
  (start [component]
    (println "Starting notifier")
    (assoc component :handler #(send-email auth %)))
  (stop [component]
    (println "Stopping notifier")
    (assoc component :notifier nil)))

(defn new-notifier [auth]
  (map->Notifier {:auth auth}))
