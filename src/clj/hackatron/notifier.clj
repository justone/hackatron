(ns hackatron.notifier
  (:require [sendgrid-clj.core :as sg]
            [hiccup.page :refer [html5]]))

(defn generate-login-email
  [params]
  ["Hackatron Login",
   (str "You can log in at http://hackatron.com/#" (:token params))
   (html5
     [:body
      [:div
       "You can log in "
       [:a {:href (str "http://hackatron.com/#" (:token params))} "here"]
       "."]])
   ])

(defn print-email
  [email]
  (println "--------------------------------------------------")
  (println (str "To: " (:to email)))
  (println (str "Subject: " (:subject email)))
  (println (str "Text: " (:text email)))
  (println (str "HTML: " (:html email)))
  (println "--------------------------------------------------"))

(defn generate-email
  [kind params]
  (condp = kind
    :login-email (generate-login-email params)))

(defn send-email
  [auth from kind to params]
  (let [[subject text html] (generate-email kind params)
        email {:to to
               :from from
               :subject subject
               :text text
               :html html}]
    (if (:api_user auth)
      (sg/send-email auth email)
      (print-email email))))
