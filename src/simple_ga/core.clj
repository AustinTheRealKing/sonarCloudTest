(ns simple-ga.core
  "Clojure simple genetic algorithm. Usage:
    lein run [-- fitness-function]

   Champlain College
   CSI-380 Spring 2019
    Austin King & Tyler Brabant"
  (:gen-class))

(require '[simple-ga.utils :as utils])
(require '[simple-ga.fitness-functions :as ff])

(defn mutate-genome
    "Produce a mutated genome by flipping each bit with mutation-rate probability.
      A coin toss is done with the specific mutation rate
     If true it will flip the bit if not it won't
     It is then mapped
    "
  
    [genome mutation-rate]
    (into [] (map (fn [bit] (if (utils/coin-toss? mutation-rate) (utils/flip-bit bit) bit)) genome)))
         
(defn crossover
    "Perform single-point crossover on the two genomes.
    
    A single genome is returned chosen from the two resulting genomes with equal
    probability.
    
    See: https://en.wikipedia.org/wiki/Crossover_(genetic_algorithm)#Single-point_crossover
    Take a random point in the smallest lenght genome
    Concat them together using the sequence up until that pivot,
    and drop the first part of the second one
    Pivot is the middle point that is randomly chosen from the smalles genome
    There is then a coin toss that will determine what parts go on the left and right 
    to make the new genome 
    "
    [genome1 genome2]
    (let [pivot (rand-int (min (count genome1) (count genome2)))]
      (if (utils/coin-toss?)
        (concat (take pivot genome1) (drop pivot genome2))
        (concat (take pivot genome2) (drop pivot genome1)))))

          
(defn reproduce
    "Create the next generation from the given parents using the relevant params.
    
    
    The new generation contains a total of (:population-size params) individuals
    including the unmodified parents (this is known as elitism).
    
    The remainder of the individuals are generated as follows:
        with probability (:crossover-rate params) two parents are chosen at random
        and crossed over and the resulting genome is mutated with mutation rate 
        (:mutation-rate params)
           otherwise
        with probability (- 1 (:crossover-rate params)) a single parent is chosen
        at random and its genome is mutated with mutation rate (:mutation-rate params)
    
    a new mutated genome is created and based on the crossover rate it is either single or
    stems from both parents
    a population is then created of these new individuals which is combined with the parents to
    finialize the newly reproduced generation
  "
  [parents params]
  (let [genome (fn [individual-genome] (if (utils/coin-toss? (:crossover-rate params))
                                         (mutate-genome (apply crossover (map (fn [genome] (get genome :genome))
                                                                           (drop 3 (shuffle parents))))
                                           (:mutation-rate params))
                                         (mutate-genome individual-genome (:mutation-rate params))))
        population (map (fn [individual] (update individual :genome #(genome %)))
                     (into [] (repeatedly (- (:population-size params)(:num-parents params)) #(rand-nth parents))))]
    
    (concat parents population)))
  
 
             

(defn generate-individual
    "Generate a single individual.
    
    Each individual is a map with key :genome associated with a genome-size length
    vector of bits (0s and 1s)
    "
    [genome-size]
    {:genome (mapv (fn [_] (rand-int 2)) 
                (range genome-size))})
    
(defn generate-population
    "Generate the population.
    
    The population is a collection of (:population-size params) individuals,
    where each individual is a map with key :genome associated with a
    (:genome-size params) length vector of bits (0s and 1s)
    Repeatedly is used to do a function a specific amount of times which
    in this case population-size
    "
    [params]
    ;; Austin King
    (repeatedly (:population-size params) #(generate-individual (:genome-size params))))
          

(defn select-parents
    "Select and return the num-parents most fit individuals in the population.
    
    This is known truncation selection.
    The parents are sorted by the highest fitness
    then the specified number of parents are taken
    "
    [population num-parents]
  
    (take num-parents (sort-by :fitness > population)))
    
(defn evaluate-individual
    "Evaluate a given individual on the specified fitness function.
    
    Returns a copy of the individual with :fitness key set to its fitness.
    
    This should evaluate the individual and set/update its fitness even if it already has a fitness value.
    
    "
    [individual fitness-function]
    (assoc individual :fitness (fitness-function (:genome individual))))
    
(defn evaluate-population
    "Evaluate the entire population in parallel.
    
    Returns a copy of the population with the :fitness key of each individual set to its fitness.
    
    This should evaluate all individuals in parallel and set/update their fitness (even ones that already have a fitness value).
    Pmap is used for concurrency to run the individual evaluates at the same time 
    "
    [population fitness-function]
    
    (pmap (fn [individual] (evaluate-individual individual fitness-function)) population))


    

(defn run-generation
    "Run a single generation of a genetic algorithm.
    
    Performs the following steps:
        (1) evaluates the current population
        (2) selects parents
        (3) builds and returns the next generation by reproducing the parents.
    "
    [population params]
    (reproduce
      (select-parents 
        (evaluate-population population (:fitness-function params)) 
        (:num-parents params))
      params))
         
(defn evolve
    "Run a genetic algorithm with the given params and return the best individual in the final generation."
    [params]
    (loop [generations-remaining (:num-generations params)
           current-population (generate-population params)]
          (if (zero? generations-remaining)
               ;; if no more generations, still need to evaluate the final population
               ;; in order to determine the best individual
              (let [evaluated-population (evaluate-population current-population (:fitness-function params))]
                   (apply max-key :fitness  evaluated-population))
               ;; otherwise move on to next generation
              (do  (println "Generation" (+ 1 (- (:num-generations params) generations-remaining)))
                   (recur (dec generations-remaining)
                          (run-generation current-population params))))))
         

(defn -main
  "Main function, run GA on max-ones problem or specified fitness function.
  
  Prints best indivdual to screen.
  "
  [& args]
  (if (> (count args) 1)
      (println "Usage:\n\tlein run [-- fitness-function]")
      (let [params {:genome-size 16
                    :population-size 100
                    :num-generations 50
                    :num-parents 5
                    :crossover-rate 0.75
                    :mutation-rate 0.1
                    :fitness-function (if (empty? args) 
                                        ff/max-ones ;; default to max-ones fitness function
                                        (resolve (symbol "simple-ga.fitness-functions" (first args))))}]
                   
       (if (:fitness-function params)
           ;; if fitness function is defined
           (println (evolve params))
           ;; otherwise invalid fitness function
           (println "unknown fitness function:" (first args)))
       (shutdown-agents)))) ;; needed in case pmap is used, otherwise program won't terminate
