(ns hackatron.ui
  (:require [hackatron.util :as util]
            [om.core :as om]
            [om-bootstrap.button :as obb]
            [om-bootstrap.input :as obi]
            [om-bootstrap.random :as obr]
            [om-bootstrap.nav :as obn]
            [om-tools.dom :as dom :include-macros true]
            [cljs.core.async :refer [put!]]))

(defn handle-change
  [e cursor korks]
  (om/update! cursor korks (.. e -target -value)))

(defn input-text
  "An input text field, with a handler."
  [name value handler & [attributes]]
  (obi/input (merge
               {:type "text"
                :label name
                :placeholder name
                :label-classname "col-xs-1"
                :wrapper-classname "col-xs-7"
                :value value
                :on-change handler}
               attributes)))

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
            {:keys [login-info]} state
            {:keys [email error]} login-info]
        (dom/div
          (dom/h3 "Login to Hackatron")
          (when error (obr/alert {:bs-style "warning"} error))
          (dom/form {:class "form-vertical"}
                    (input-text
                      "Email" email
                      #(handle-change % state [:login-info :email])
                      {:id "email-field" :bs-style (if error "error" "standard") :label nil})
                    (input-button
                      "Send Login Email"
                      #(put! actions [:hackatron/send-email]))))))))

(defn email-sent [state owner]
  (reify
    om/IDisplayName (display-name [this] "EmailSent")
    om/IRender
    (render [this]
      (let [{:keys [email]} state]
        (dom/div
          (dom/h3 "Email sent!")
          (dom/div "Email sent to " (dom/b email) ", please check it for your login link."))))))

(defn add [state owner]
  (reify
    om/IDisplayName (display-name [this] "AddView")
    om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)]
        (dom/div
          (dom/h2 (str "Count is: " (:count state)))
          (input-button "Add" #(put! actions [:hackatron/add])))))))

(defn profile-main [state owner]
  (reify
    om/IDisplayName (display-name [this] "ProfileMain")
    om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)
            {:keys [profile]} state
            {:keys [name access error]} profile]
        (dom/div
          (dom/form {:class "form-horizontal"}
                    (input-text
                      "Email"
                      (:uid state)
                      identity
                      {:id "email-field" :disabled true})
                    (input-text
                      "Name"
                      name
                      #(handle-change % state [:profile :name])
                      {:id "name-field" :bs-style (if error "error" "default") :feedback? true})
                    (input-button "Save" #(put! actions [:hackatron/save-profile]))))))))

(defn link-msg [e actions message]
  (put! actions message)
  (.preventDefault e)
  nil)

(defn top [state owner]
  (reify
    om/IDisplayName (display-name [this] "TopView")
    om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)]
        (dom/div
          (obn/navbar
            {:brand "Hackatron"}
            (obn/nav
              {:collapsible? true}
              (obn/nav-item {:key 1 :href "#" :on-click #(link-msg % actions [:hackatron/section :add])} "Add")
              (obn/nav-item {:key 1 :href "#" :on-click #(link-msg % actions [:hackatron/section :sign_up/select])} "Sign Up")
              (when false (obb/dropdown {:navbar "right" :key 2, :title "Admin"}
                            (obb/menu-item {:key 1 :on-click #(link-msg % actions [:hackatron/section :hackathon/add])} "Add Hackathon")))
              (obn/nav-item {:key 1 :href "#" :on-click #(link-msg % actions [:hackatron/section :profile/main])} "Profile")))
          (case (:state state)
            :add (om/build add state)
            :profile/main (om/build profile-main state)
            (dom/div (str (:state state)))))))))

(defn main-view [state owner]
  (reify
    om/IDisplayName (display-name [this] "MainView")
    om/IRender
    (render [this]
      (case (:state state)
        :login (om/build login state)
        :logging-in (dom/div "Logging in...")
        :email-sent (om/build email-sent state)
        (om/build top state)))))
