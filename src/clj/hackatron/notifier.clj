(ns hackatron.notifier
  (:require [sendgrid-clj.core :as sg]))

(defn send-email
  [auth email]
  (sg/send-email auth email))
