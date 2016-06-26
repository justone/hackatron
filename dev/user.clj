(ns user
  (:require 
   [system.repl :refer [system set-init! start stop reset]]
   [hackatron.systems :refer [dev-system]]))

(set-init! #'dev-system)
; type (start) in the repl to start your development-time system.
