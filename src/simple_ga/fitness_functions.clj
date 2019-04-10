(ns simple-ga.fitness-functions
  (:gen-class))

(require '[simple-ga.utils :as utils])

(defn max-ones
    "The max-ones fitness function just returns the sum of the bitstring."
    [genome]
    (reduce + genome))
    
(defn simple-equation
    "The simple-equation tries to find x and y that maximize 6x - x^2 + 4y - y^2.
    
    Assumes the genome is 16 bit vector, first 8 bits represent x, second 8 bits represent y.
    "
    [genome]
    (assert (= 16 (count genome)) "Genomes for simple-equation must be 16 bits long")
    (let [x (utils/to-int (subvec genome 0 8))
          y (utils/to-int (subvec genome 8 16))]
         (+ (* 6 x)
            (* -1 (* x x))
            (* 4 y)
            (* -1 (* y y)))))
