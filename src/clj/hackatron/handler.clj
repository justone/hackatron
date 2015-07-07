(ns hackatron.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET POST]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.util.response :refer [response redirect]]
   [taoensso.timbre    :as timbre :refer (tracef debugf infof warnf errorf)]
   [hackatron.data :refer :all]
   [reloaded.repl :refer [system]]))

(defroutes routes
  ;; load the UI
  (GET "/" [] (redirect "/index.html"))

  ;; test routes
  (GET "/send" {:keys [services params]} (do
                                            ((:notifier services) {:to "nate@endot.org" :from "nate@endot.org" :subject "Test email 2" :text "Test Email" :html "<h1>Test Email</h1>"})
                                            (response "sent")))
  (GET "/inc" {:keys [services params]} (do
                                          (dset (:data services) "other" {:foo "bar" :set #{true false}})
                                          (response "set")))

  ;; sente specific
  (GET  "/dump"  req (str @(:connected-uids (:sente system))))
  (GET  "/chsk"  req ((:ring-ajax-get-or-ws-handshake (:sente system)) req))
  (POST "/chsk"  req ((:ring-ajax-post (:sente system)) req))

  ;; login handler
  ; (POST "/login" req (login! req))
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


(defmulti event-msg-handler :id) ; Dispatch on event-id
(defn     event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  (debugf "Event: %s" event)
  (event-msg-handler ev-msg))

(defmethod event-msg-handler :default ; Fallback
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    (debugf "Unhandled event: %s" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))
