(ns sysfig.components.carmine
  (:require [com.stuartsierra.component :as component]
            [taoensso.carmine :as car :refer [wcar]]))

(defrecord Carmine [conn spec pool]
  component/Lifecycle
  (start [component]
    (println "Starting carmine")
    (assoc component :conn {:spec spec :pool pool}))
  (stop [component]
    (println "Stopping carmine")
    (assoc component :conn nil)))

(defn new-carmine
  ([]
   (map->Carmine {:spec {} :pool {}}))
  ([host port]
   (map->Carmine {:spec {:host host :port port} :pool {}})))
