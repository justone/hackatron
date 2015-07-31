(ns hackatron.core
  (:require [hackatron.util :as util]
            [om.core :as om]
            [hackatron.ui :as ui]
            [taoensso.sente :as sente :refer (cb-success?)]
            [cljs.core.async :refer [chan <! put!]]))

(.log js/console  "Hello from Clojurescript!")

(defonce actions (chan))
(defonce app-state (atom {
                          :state :login
                          :name ""
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

(defmulti event-msg-handler :id) ; Dispatch on event-id
(defn     event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  (.log js/console "Event: %s" (str event))
  (event-msg-handler ev-msg))

(defmethod event-msg-handler :default ; Fallback
  [{:as ev-msg :keys [event]}]
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
  (util/log (str "acting on " (str topic)))
  [topic])

(defmulti action-dispatcher! dispatcher-fn)

(defmethod action-dispatcher! :default
  [[topic message]]
  (util/log (str "no dispatching for " topic)))

(defmethod action-dispatcher! [:hackatron/login]
  [[topic message :as act]]
  (util/log (str "Logging in " (str act))))

;; TODO: integrate into dispatcher
; (sender [:hackatron/button {:foo "bar"}])
; (sender [:hackatron/button2 {:bar "foo"}] 5000 (fn [cb-reply] (.log js/console "Callback reply: %s" (str cb-reply))))

(defonce actionchan_ (atom nil))
(defn stop-action-dispatcher! [] (when-let [ch @actionchan_] (put! actions [:stop])))
(defn start-action-dispatcher! []
  (stop-action-dispatcher!)
  (reset! actionchan_ (util/action-loop action-dispatcher! actions)))

(defn start! []
  (start-action-dispatcher!)
  (show-gui!)
  (start-router!))

(start!)
