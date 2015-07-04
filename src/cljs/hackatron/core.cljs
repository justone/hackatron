(ns hackatron.core
  (:require [om.core :as om]
            [hackatron.ui :as ui]
            [cljs.core.async :refer [chan <!]]))

(.log js/console  "Hello from Clojurescript!")

(defonce actions (chan))
(defonce app-state (atom {}))

(defn show-gui! []
  (om/root
    ui/main-view
    app-state
    {:target (. js/document (getElementById "app"))
     :shared {:actions actions}}))

(defn start! []
  (show-gui!))

(start!)
