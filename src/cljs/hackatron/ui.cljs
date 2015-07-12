(ns hackatron.ui
  (:require [hackatron.util :as util]
            [om.core :as om]
            [om-bootstrap.button :as obb]
            [om-bootstrap.input :as obi]
            [om-tools.dom :as dom :include-macros true]
            [cljs.core.async :refer [put!]]))

(defn handle-change
  [e cursor k]
  (om/update! cursor [k] (.. e -target -value)))

(defn login [state owner]
  (reify
    om/IDisplayName (display-name [this] "LoginView")
    om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)
            {:keys [name email]} state]
        (dom/div
          (obi/input {:type "text" :placeholder "Name" :value name :on-change #(handle-change % state :name)})
          (obi/input {:type "text" :placeholder "Email" :value email :on-change #(handle-change % state :email)})
          (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (put! actions [:hackatron/login {:foo "bar"}]) nil) } "Login"))))))

(defn main-view [state owner]
  (reify
    om/IDisplayName (display-name [this] "MainView")
    om/IRender
    (render [this]
      (case (:state state)
        :login (om/build login state)))))
