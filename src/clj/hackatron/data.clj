(ns hackatron.data)

(defprotocol DataStore
  "A protocol for data storage"
  (dset [this k data] "Store data at the specified key k.")
  (dget [this k] "Retrieve data for the specified key k."))
