(ns hackatron.util)

(defn log
  "Logs data to the console."
  [s]
  (.log js/console s))
