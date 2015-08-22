(ns hackatron.core
  (:require [hackatron.util :as util]
            [om.core :as om]
            [clojure.string :as string]
            [hackatron.ui :as ui]
            [taoensso.sente :as sente :refer (cb-success?)]
            [cljs.core.async :refer [chan <! put!]]))

(enable-console-print!)
(print "Hello from Clojurescript!")

(defonce actions (chan))
(defonce app-state (atom {
                          :state :login
                          :email ""
                          }))

;; set up sente
(let [chsk-type :auto
      {:keys [chsk ch-recv send-fn state]} (sente/make-channel-socket! "/chsk" {:type chsk-type})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

(defn event-dispatcher-fn [[topic data]]
  ; (util/log (str "from server, acting on " (str topic)))
  [topic])

(defmulti event-msg-handler event-dispatcher-fn)
(defn     event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  ; (.log js/console "Event: %s" (str ?data))
  ; (.log js/console "Id: %s" (str id))
  ; dispatch on the data received
  (event-msg-handler ?data))

(defmethod event-msg-handler [:hackatron/state]
  [[event data]]
  ; (.log js/console "Update to state!" (str event))
  (swap! app-state assoc :count (:count data)))

(defmethod event-msg-handler :default ; Fallback
  [event]
  (.log js/console "Unhandled event: %s" (str event)))

(defonce router_ (atom nil))
(defn stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (stop-router!)
  (reset! router_ (sente/start-chsk-router! ch-chsk event-msg-handler*)))
;; done setting up sente

(defn show-gui! []
  (.log js/console "Loading GUI")
  (om/root
    ui/main-view
    app-state
    {:target (. js/document (getElementById "app"))
     :shared {:actions actions :send chsk-send!}}))

(defn dispatcher-fn [[topic message]]
  ; (util/log (str "acting on " (str topic)))
  [topic])

(defmulti action-dispatcher! dispatcher-fn)

;; TODO: integrate into dispatcher
; (sender [:hackatron/button {:foo "bar"}])
; (sender [:hackatron/button2 {:bar "foo"}] 5000 (fn [cb-reply] (.log js/console "Callback reply: %s" (str cb-reply))))

(defmethod action-dispatcher! [:hackatron/add]
  [[topic message]]
  (chsk-send! [:hackatron/add {}]))

(defmethod action-dispatcher! [:hackatron/get]
  [[topic message]]
  (chsk-send! [:hackatron/get {}]))

(defmethod action-dispatcher! :default
  [[topic message]]
  (util/log (str "no dispatching for " topic)))

(defmethod action-dispatcher! [:hackatron/send-email]
  [[topic message :as act]]
  (do
    (sente/ajax-call "/send_login_email"
                     {:method :post
                      :params {:email-address  (str (:email @app-state))
                               :csrf-token (:csrf-token @chsk-state)}}
                     (fn [ajax-resp]
                       (swap! app-state assoc :state :email-sent)))))


(defonce actionchan_ (atom nil))
(defn stop-action-dispatcher! [] (when-let [ch @actionchan_] (put! actions [:stop])))
(defn start-action-dispatcher! []
  (stop-action-dispatcher!)
  (reset! actionchan_ (util/action-loop action-dispatcher! actions)))

(defn login! [_ _ _ new-chsk-state]
  (let [uid (:uid new-chsk-state)
        url-hash (.-hash (.-location js/document))
        login-token (string/replace url-hash #"#" "")
        _ (set! (.-hash (.-location js/document)) "")]
    ; (print (str "new: " new-chsk-state))
    (if (= uid :taoensso.sente/nil-uid)
      (when (seq url-hash)
        (println "attempting to log in!")
        (sente/ajax-call "/login"
                         {:method :post
                          :params {:login-token login-token
                                   :csrf-token (:csrf-token new-chsk-state)}}
                         (fn [ajax-resp]
                           (when (= (:?status ajax-resp) 200)
                             (println "logged in now!")
                             (swap! app-state assoc :state :logged-in :uid uid)
                             (sente/chsk-reconnect! chsk)))))
      (do
        (println "already logged in!")
        (swap! app-state assoc :state :logged-in :uid uid)
        (remove-watch chsk-state :login)))))

(defn start! []
  (start-action-dispatcher!)
  (start-router!)
  ;; must watch chsk-state because it doesn't connect right away
  (add-watch chsk-state :login login!)
  (show-gui!))

(start!)
