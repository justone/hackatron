(ns hackatron.ui
  (:require [om.core :as om]
            [om-bootstrap.button :as obb]
            [om-tools.dom :as dom :include-macros true]))

(defn main-view [app owner]
  (reify
    om/IDisplayName (display-name [this] "MainView")
    om/IRender
    (render [this]
      (dom/div
        (obb/button { :bs-style "success" :on-click (fn [e] (.preventDefault e) (.log js/console "Button pushed!") nil) } "Button!")))))
