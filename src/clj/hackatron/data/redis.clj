(ns hackatron.data.redis
  (:require [hackatron.data :as data]
            [taoensso.carmine :as car :refer (wcar)]))

(defrecord RedisData [opts]
  data/DataStore
  (dset [this k data]
    (car/wcar (:opts this) (car/set (name k) data)))
  (dget [this k]
    (car/wcar (:opts this) (car/get (name k)))))
