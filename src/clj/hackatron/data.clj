(ns hackatron.data)

(defprotocol DataStore
  "A protocol for data storage"
  (new-email-token [this email] "Create and store new token for the given email address, return the token.")
  (check-email-token [this token] "Check the token against the database and return the email address")
  (get-counter [this] "Retrieve the counter")
  (inc-counter [this] "Increment the counter"))
