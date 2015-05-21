(ns example.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [example.html :as html]))

(defroutes routes
  (GET "/test" [] (html/index))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> routes 
      (wrap-defaults site-defaults)))
