(ns hackatron.utils)

(defn random-string
  [len]
  (let [ascii-codes (lazy-cat (range 48 57) (range 65 90) (range 97 122))
        chars (map char ascii-codes)
        rand-chars (take len (repeatedly #(rand-nth chars)))]
    (reduce str rand-chars)))
