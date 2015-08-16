(ns hackatron.components.notifier
  (:require [com.stuartsierra.component :as component]
            [hackatron.notifier :refer [send-email]]))

(defrecord Notifier [auth handler from]
  component/Lifecycle
  (start [component]
    (println "Starting notifier")
    (assoc component :handler (partial send-email auth from)))
  (stop [component]
    (println "Stopping notifier")
    (assoc component :notifier nil)))

(defn new-notifier
  ([]
   (new-notifier {} ""))
  ([auth from]
   (map->Notifier {:auth auth :from from})))
