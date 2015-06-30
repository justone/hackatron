(ns hackatron.components.notifier
  (:require [com.stuartsierra.component :as component]))

(defrecord Notifier [address handler]
  component/Lifecycle
  (start [component]
    (println (str "Starting notifier: " address))
    (assoc component :handler #(println (str "Sending to address: " address))))
  (stop [component]
    (println (str "Stopping notifier: " address))
    (assoc component :notifier nil)))

(defn new-notifier [address]
  (map->Notifier {:address address}))
