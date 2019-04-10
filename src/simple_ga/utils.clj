(ns simple-ga.utils
  (:gen-class))
  
  
(defn coin-toss?
    "Toss a biased-coin.
    
    Returns true with specified probability (defaults to 0.5), otherwise false."
    ([] (coin-toss? 0.5))
    ([probability] (< (rand) probability)))
    
(defn flip-bit
    "Flips given bit (if 0 returns 1, if 1 returns 0)."
    [bit]
    (if (== bit 0) 1 0))
    
(defn to-int
    "Convert a bit vector to a signed int using 1s complement."
    [bit-vector]
    (let [bit-string (clojure.string/join "" (rest bit-vector))
          unsigned (Integer/parseInt bit-string 2)]
         (if (zero? (first bit-vector))
             unsigned
             (* -1 unsigned))))
