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

(defn input-text
  "An input text field, with a handler."
  [name value handler]
  (obi/input {:type "text" :placeholder name :value value :on-change handler}))

(defn input-button
  "An input button, with a handler.  Prevents default event handling."
  [text handler]
  (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (handler) nil) } text))

(defn login [state owner]
  (reify
    om/IDisplayName (display-name [this] "LoginView")
    om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)
            {:keys [name email]} state]
        (dom/div
          (dom/h3 "Login")
          (input-text "Name" name #(handle-change % state :name))
          (input-text "Email" email #(handle-change % state :email))
          (input-button "Send Login Email" #(put! actions [:hackatron/login {:foo "bar"}])))))))

(defn main-view [state owner]
  (reify
    om/IDisplayName (display-name [this] "MainView")
    om/IRender
    (render [this]
      (case (:state state)
        :login (om/build login state)))))
