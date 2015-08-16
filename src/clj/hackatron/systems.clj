(ns hackatron.systems
  (:require [system.core :refer [defsystem]]
            (system.components
             [repl-server :refer [new-repl-server]]
             [sente :refer [new-channel-sockets]])
            (hackatron.components
             [notifier :refer [new-notifier]]
             [handler :refer [new-handler]]
             [data :refer [new-data]]
             [web :refer [new-web-server]])
            [environ.core :refer [env]]
            [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]
            [hackatron.handler :refer [event-msg-handler*]]
            [com.stuartsierra.component :as component]))

(defn dev-system
  []
  (component/system-map
    :notifier (new-notifier)
    :handler (component/using (new-handler) [:notifier :data])
    :data (new-data)
    :web (component/using (new-web-server (Integer. (env :http-port))) [:handler])
    :sente (new-channel-sockets event-msg-handler* sente-web-server-adapter)))

(defn prod-system
  []
  (component/system-map
    :notifier (new-notifier {:api_user (env :sendgrid-user) :api_key (env :sendgrid-password)} (env :email-from))
    :handler (component/using (new-handler) [:notifier])
    :web (component/using (new-web-server (Integer. (env :http-port))) [:handler])
    :repl-server (new-repl-server (Integer. (env :repl-port)))
    :sente (new-channel-sockets event-msg-handler* sente-web-server-adapter)))
