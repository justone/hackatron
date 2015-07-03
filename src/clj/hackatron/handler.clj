(ns hackatron.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.util.response :refer [response]]
   [taoensso.carmine :as car :refer [wcar]]
   [hackatron.html :as html]))

(defroutes routes
  (GET "/test" [] (html/index))
  (GET "/test2" {:keys [services params]} (do
                                            ((:notifier services))
                                            (str (:address (:notifier services)))))
  (GET "/inc" {:keys [services params]} (do
                                          (wcar (:carmine services) (car/set "another" {:foo "bar" :set #{true false}}))
                                          (response "incremented")))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-services
  [f services]
  (fn [req]
    (f (assoc req :services services))))

(defn make-handler
  [services]
  (-> routes
      (wrap-services services)
      (wrap-defaults site-defaults)))
