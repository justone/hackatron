(ns hackatron.html
  (:require 
    (hiccup [page :refer [html5 include-js include-css]])))

(defn index []
  (html5 [:head
          (include-css
            "css/bootstrap.css"
            "css/bootstrap-theme.css"
            "css/style.css"
            )]
         [:body
          [:div#app.container]
          (include-js "cljs/main.js")]))
