(ns hackatron.model
  (:require
   [hackatron.data :as data]))


(defn get-profile [data email]
  (if-let [profile (data/get-profile data email)]
    profile
    (let [profile {:access :normal
                   :name ""}]
      (data/set-profile data email profile))))
