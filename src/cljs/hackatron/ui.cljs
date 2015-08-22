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
  [name value handler & [attributes]]
  (obi/input (merge {:type "text" :placeholder name :value value :on-change handler} attributes)))

(defn input-button
  "An input button, with a handler.  Prevents default event handling."
  [text handler]
  (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (handler) nil) } text))

(defn login [state owner]
  (reify
    om/IDisplayName (display-name [this] "LoginView")
    om/IDidMount
    (did-mount [this]
      (.focus (.getElementById js/document "email-field")))
    om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)
            {:keys [email]} state]
        (dom/div
          (dom/h3 "Login to Hackatron")
          (input-text "Email" email #(handle-change % state :email) {:id "email-field"})
          (input-button "Send Login Email" #(put! actions [:hackatron/send-email])))))))

(defn email-sent [state owner]
  (reify
    om/IDisplayName (display-name [this] "EmailSent")
    om/IRender
    (render [this]
      (let [{:keys [email]} state]
        (dom/div
          (dom/h3 "Email sent!")
          (dom/div "Email sent to " (dom/b email) ", please check it for your login link."))))))

(defn top [state owner]
  (reify
    om/IDisplayName (display-name [this] "TopView")
    om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)]
        (dom/div
          (dom/h3 (str "Logged in as " (:uid state) "."))
          (dom/h2 (str "Count is: " (:count state)))
          (input-button "Add" #(put! actions [:hackatron/add])))))))

(defn main-view [state owner]
  (reify
    om/IDisplayName (display-name [this] "MainView")
    om/IRender
    (render [this]
      (case (:state state)
        :login (om/build login state)
        :email-sent (om/build email-sent state)
        :logged-in (om/build top state)))))
