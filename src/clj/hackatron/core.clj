(ns hackatron.core
  (:gen-class)
  (:require 
   [system.repl :refer [set-init! start]]
   [hackatron.systems :refer [prod-system]]))

(defn -main
  "Start a production system."
  [& args]
  (set-init! prod-system)
  (start))
