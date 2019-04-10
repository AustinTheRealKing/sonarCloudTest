(ns simple-ga.core-test
  (:require [clojure.test :refer :all]
            [simple-ga.core :refer :all]))

(require '[simple-ga.fitness-functions :as ff])

(deftest mutate-test
  (testing "mutate-genome not working correctly."
    (is (= (mutate-genome [1 0 0 1] 1) [0 1 1 0]))
    (is (= (mutate-genome [1 0 0 1] 0) [1 0 0 1]))
    (is (= (mutate-genome [1 1 1 0 1] 1) [0 0 0 1 0]))
    (is (= (mutate-genome [1 1 1 0 1] 0) [1 1 1 0 1]))
    (is (= (mutate-genome [0 0 0 0 0 0 0 1] 1) [1 1 1 1 1 1 1 0]))))
            
(deftest crossover-test
  (testing "crossover not working correctly."
    (let [genome1 [1 0 0 0]
          genome2 [0 0 0 1]
          result (crossover genome1 genome2)]
         (is 
           (or
             (= result [1 0 0 0])
             (= result [1 0 0 1])
             (= result [0 0 0 1])
             (= result [0 0 0 0]))))))

(deftest selection-test
  (testing "select-parents not working correctly."
    (let [population '({:fitness 3}, {:fitness 10}, {:fitness 100}, {:fitness -34}, {:fitness 2})]
      ;; convert to set to ignore ordering
      (is (= (set (select-parents population 1)) (set '({:fitness 100}))))
      (is (= (set (select-parents population 2)) (set '({:fitness 100}, {:fitness 10}))))
      (is (= (set (select-parents population 3)) (set '({:fitness 100}, {:fitness 10}, {:fitness 3})))))))

(let [params {:genome-size 16
              :population-size 100
              :num-generations 50
              :num-parents 5
              :crossover-rate 0.75
              :mutation-rate 0.1
              :fitness-function ff/max-ones}]

  (deftest generate-population-test
    (testing "generate-population not working correctly."
      (let [population (generate-population params)]
        (is (= (count population) (:population-size params)))
        (is (every? (fn [individual] (= (count (:genome individual)) (:genome-size params))) 
              population))
        (is (not (apply = population)))))) ;; check not all the same individual


  ;; testing that reproduce keeps the parents and fills in the rest of the population
  ;; with new individuals
  ;; TODO: this test could be improved

  (deftest reproduce-test
    (testing "reproduce not working correctly."
      (let [population (generate-population params)
            parents (take (:num-parents params) population)
            new-generation (reproduce parents params)
            new-generation-set (set new-generation)]
        (is (= (count new-generation) (:population-size params)))
        (is (every? (fn [parent] (contains? new-generation-set parent))
              parents)))))  

           
  ;; testing whole GA

  (deftest ga-test-1
    (testing "ga unable to solve max-ones problem"
      (let [result (evolve params)]
        (is (= (:genome result) [1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1]))
        (is (= (:fitness result) 16)))))
       
  (deftest ga-test-2
    (testing (str "ga unable to solve simple-equation.\n"
                  "Note: you may occasionally fail this due to being unlucky, so "
                  "if you believe you have everything correct, try again.")
    (let [result (evolve (assoc params :fitness-function ff/simple-equation))]
         (is (= (:genome result) [0 0 0 0 0 0 1 1 0 0 0 0 0 0 1 0]))
         (is (= (:fitness result) 13)))))

) ;; close params let
