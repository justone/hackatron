(ns hackatron.handler
  (:require
   [compojure.route :as route]
   [compojure.core :refer [defroutes GET POST]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.util.response :refer [response redirect]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [taoensso.timbre    :as timbre :refer (tracef debugf infof warnf errorf)]
   [hackatron.data :refer [dset dget]]
   [hackatron.html :as html]
   [hackatron.utils :as utils]
   [reloaded.repl :refer [system]]))

; temporary server side state
(defonce server-state (atom {:count 0}))

; TODO: check for validity of email domain
(defn send-login-email! [params data notifier]
  (let [login-token (utils/random-string 32)
        email (:email-address params)]
    (do
      (dset data (str "login:" login-token) email)
      (notifier :login-email email {:token login-token})))
  {:status 200})

; TODO: remove the login token once used
(defn login! [session params data]
  (let [login-token (:login-token params)]
    (when-let [email (dget data (str "login:" login-token))]
      {:status 200 :session  (assoc session :uid email)})))

(defroutes routes
  ;; load the UI
  (GET "/" [] (html/index))

  ;; handle login
  (POST "/send_login_email" {:keys [services params]}
        (send-login-email!
          params
          (:data services)
          (:notifier services)))
  (POST "/login" {:keys [session services params]}
        (login!
          session
          params
          (:data services)))

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

; TODO: allow session key to be specified in env
(defonce session-key (utils/random-string 16))
(defn make-handler
  [services]
  (let [ring-defaults-config
        (-> site-defaults
            (assoc-in [:security :anti-forgery] {:read-token (fn [req] (-> req :params :csrf-token))})
            (assoc-in [:session :store] (cookie-store {:key session-key})))]
    (-> routes
        (wrap-services services)
        (wrap-defaults ring-defaults-config))))


(defmulti event-msg-handler :id) ; Dispatch on event-id
(defn     event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  ; (debugf "Event: %s" event)
  (event-msg-handler ev-msg))

(defmethod event-msg-handler :hackatron/add
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)
        sender  (:chsk-send! (:sente system))
        uids    (:any @(:connected-uids (:sente system)))]
    (debugf "Add event called: %s" @(:connected-uids (:sente system)))
    (swap! server-state (fn [prev] (update-in prev [:count] inc)))
    (dorun (map #(sender % [:hackatron/state @server-state]) uids))))

(defmethod event-msg-handler :default ; Fallback
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    ; (debugf "Unhandled event: %s" event)
    ; (debugf "from: %s" (:session (:ring-req ev-msg)))
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))
