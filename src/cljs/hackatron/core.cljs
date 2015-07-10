(ns hackatron.core
  (:require [om.core :as om]
            [hackatron.ui :as ui]
            [taoensso.sente :as sente :refer (cb-success?)]
            [cljs.core.async :refer [chan <!]]))

(.log js/console  "Hello from Clojurescript!")

(defonce actions (chan))
(defonce app-state (atom {
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

(def router_ (atom nil))
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

(defn start! []
  (stop-router!)
  (show-gui!)
  (start-router!))

(start!)
