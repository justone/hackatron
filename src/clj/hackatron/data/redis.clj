(ns hackatron.data.redis
  (:require [hackatron.data :as data]
            [hackatron.utils :as utils]
            [taoensso.carmine :as car :refer (wcar)]))

(defrecord RedisData [opts]
  data/DataStore
  (new-email-token [this email]
    (let [login-token (utils/random-string 32)]
      (car/wcar (:opts this) (car/set (str "login-token:" login-token) email))
      login-token))
  (check-email-token [this token]
    (when-let [email (car/wcar (:opts this) (car/get (str "login-token:" token)))]
      email))
  (get-counter [this]
    (car/wcar (:opts this) (car/get "counter")))
  (inc-counter [this]
    (car/wcar (:opts this) (car/incr "counter"))))
