# Clojure Simple Genetic Algorithm
Clojure implementation of a simple genetic algorithm.

In a genetic algorithm, a population of candidate solutions (called individuals, creatures, or phenotypes) to an optimization problem is evolved toward better solutions. Each candidate solution has a set of properties (its chromosomes or genotype) which can be mutated and altered; here, solutions are represented in binary as vectors of 0s and 1s.

Evolution starts from a population of randomly generated individuals, and is an iterative process, with the population in each iteration called a generation. In each generation, the fitness of every individual in the population is evaluated; the fitness is usually the value of the objective function in the optimization problem being solved. The more fit individuals are selected from the current population (here we use truncation selection) and modified copies of these parents' genomes (recombined and mutated) form a new generationst. The new generation of candidate solutions is then used in the next iteration of the algorithm. The algorithm terminates after a fixed number of generations has elapsed.

See https://en.wikipedia.org/wiki/Genetic_algorithm


## Layout
You will need to add code to `src/simple_ga/core.clj`, 
Do not change the function `-main` nor any of the unit tests. Please use all of our existing function signatures, but feel free to add your own as well.

`src/simple_ga/utils.clj` has utility functions that will be useful for your implementation
`src/simple_ga/fitness_functions.clj` is where fitness functions are defined

## Running
Once your program is implemented, you can run it by executing `lein run [-- fitness-function]` from the current directory.

For example
```
$ lein run -- max-ones
```


## Testing
Test the program by executing `lein test` from this directory. 
It will test each of the functions you are supposed to implement.
