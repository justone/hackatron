(ns hackatron.components.data
  (:require [com.stuartsierra.component :as component]
            [hackatron.data.redis :as redis]))

(defrecord Data [opts kind data]
  component/Lifecycle
  (start [component]
    (println "Starting data")
    ;; TODO: support other data backends
    (assoc component :data (redis/->RedisData opts)))
  (stop [component]
    (println "Stopping data")
    (assoc component :data nil)))

(defn new-data
  ([]
   (new-data "redis" {:spec {} :pool {}}))
  ([kind opts]
   (map->Data {:kind kind :opts opts})))
