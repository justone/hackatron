(ns hackatron.ui
  (:require [om.core :as om]
            [om-bootstrap.button :as obb]
            [om-tools.dom :as dom :include-macros true]))

(defn main-view [app owner]
  (reify
    om/IDisplayName (display-name [this] "MainView")
    om/IRender
    (render [this]
      (let [sender (om/get-shared owner :send)]
        (dom/div
          (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (do (sender [:hackatron/button {:foo "bar"}]) (.log js/console "Button pushed!")) nil) } "Button!")
          (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (do (sender [:hackatron/button2 {:bar "foo"}] 5000 (fn [cb-reply] (.log js/console "Callback reply: %s" (str cb-reply)))) (.log js/console "Button 2 pushed!")) nil) } "Button 2!"))))))
