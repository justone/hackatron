(ns hackatron.util
  (:require-macros
    [cljs.core.async.macros :refer [go-loop]]))

(defn log
  "Logs data to the console."
  [s]
  (.log js/console s))

(defn action-loop [fun channel]
  (go-loop []
           (let [act (<! channel)]
             (when (not= (first act) :stop)
               (fun act)
               (recur)))))
