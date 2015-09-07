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
    (car/wcar (:opts this) (car/incr "counter")))
  (all-profiles [this]
    (->> (car/wcar (:opts this) (car/keys "profile:*"))
         (map #(clojure.string/replace % #"profile:" ""))
         (map #(vector % (data/get-profile this %)))
         (into {})))
  (set-profile [this email profile]
    (car/wcar (:opts this) (car/set (str "profile:" email) profile))
    profile)
  (get-profile [this email]
    (when-let [profile (car/wcar (:opts this) (car/get (str "profile:" email)))]
      profile)))
