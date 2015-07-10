(ns hackatron.ui
  (:require [hackatron.util :as util]
            [om.core :as om]
            [om-bootstrap.button :as obb]
            [om-bootstrap.input :as obi]
            [om-tools.dom :as dom :include-macros true]))

(defn handle-change
  [e cursor k]
  (om/update! cursor [k] (.. e -target -value)))

(defn main-view [app owner]
  (reify
    om/IDisplayName (display-name [this] "MainView")
    om/IRender
    (render [this]
      (let [sender (om/get-shared owner :send)
            {:keys [name email]} app]
        (dom/div
          (obi/input {:type "text" :placeholder "Name" :value name :on-change #(handle-change % app :name)})
          (obi/input {:type "text" :placeholder "Email" :value email :on-change #(handle-change % app :email)})
          (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (do (sender [:hackatron/button {:foo "bar"}]) (.log js/console "Button pushed!")) nil) } "Button!")
          (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (do (sender [:hackatron/button2 {:bar "foo"}] 5000 (fn [cb-reply] (.log js/console "Callback reply: %s" (str cb-reply)))) (.log js/console "Button 2 pushed!")) nil) } "Button 2!"))))))
