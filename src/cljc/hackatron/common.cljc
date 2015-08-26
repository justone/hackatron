(ns hackatron.common)

(defn valid-email?
  "Checks if the passed in string could pass as an email address"
  [email]
  (re-matches #"^\w+@[\w.]+$" email))
