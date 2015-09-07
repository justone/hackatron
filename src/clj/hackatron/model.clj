(ns hackatron.model
  (:require
   [hackatron.data :as data]))


(defn get-profile
  "Retrieve profile from the database.  If one doesn't exist a new one is created and saved."
  [data email]
  (if-let [profile (data/get-profile data email)]
    profile
    (let [profile {:access :normal
                   :name ""}]
      (data/set-profile data email profile))))

(defn set-profile
  "Save profile to the database.  If there are no admins, the user is granted admin access."
  [data email profile]
  (let [admin-count   (count (filter #(= :admin (:access (second %))) (data/all-profiles data)))
        make-admin    (= admin-count 0)
        new-profile   (if make-admin (merge profile {:access :admin}) profile)]
    (data/set-profile data email new-profile)))
