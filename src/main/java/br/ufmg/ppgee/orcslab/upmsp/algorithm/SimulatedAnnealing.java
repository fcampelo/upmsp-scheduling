package br.ufmg.ppgee.orcslab.upmsp.algorithm;

import br.ufmg.ppgee.orcslab.upmsp.neighborhood.*;
import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;
import br.ufmg.ppgee.orcslab.upmsp.util.Timer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This algorithm implements a heuristic based on the simulated annealing. This implementation
 * follows the simulated annealing proposed by [1].
 *
 * <p>[1] Santos et al. Analysis of stochastic local search methods for the unrelated parallel
 * machine scheduling problem. International Transactions in Operational Research, 2016.
 * DOI: https://doi.org/10.1111/itor.12316
 *
 * <p>[2] Vallada and Ruiz. A genetic algorithm for the unrelated parallel machine scheduling
 * problem with sequence dependent setup times. European Journal of Operational Research 211,
 * 3, pp. 612-622, 2011.
 */
public class SimulatedAnnealing extends AbstractAlgorithm {

    /**
     * To prevent stagnation, reheating is performed when the temperature reaches this
     * minimum threshold.
     */
    private static double EPS = 1e-6;

    @Override
    protected Solution doSolve(Problem problem, Random random, Map<String, Object> parameters) {
        return doSolve(problem, random, parameters, null);
    }

    /**
     * Implement the simulated annealing proposed by [1] that solves the unrelated parallel machine
     * scheduling problem with sequence dependent setup times.
     * @param problem The problem instance.
     * @param random A random number generator.
     * @param parameters Algorithm parameters.
     * @param callback A callback object.
     * @return A solution of the problem.
     */
    public Solution solve(Problem problem, Random random, Map<String, Object> parameters, Callback callback) {

        // Initialize the map of parameters if it is null
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        // Initialize the random number generator if it is null
        if (random == null) {
            random = new Random();
        }

        // Set the seed of the random number generator
        if (parameters.containsKey("seed")) {
            Number seed = (Number) parameters.get("seed");
            random.setSeed(seed.longValue());
        }

        return doSolve(problem, random, parameters, callback);
    }

    /**
     * Implement the simulated annealing proposed by [1] that solves the unrelated parallel machine
     * scheduling problem with sequence dependent setup times.
     * @param problem The problem instance.
     * @param random A random number generator.
     * @param parameters Algorithm parameters.
     * @param callback A callback object.
     * @return A solution of the problem.
     */
    protected Solution doSolve(Problem problem, Random random, Map<String, Object> parameters, Callback callback) {

        // Compute default time limit according to [2] with the multiplier t = 50.
        long t = 50;
        long defaultTime = problem.n * (problem.m / 2) * t;

        // Get heuristic parameters
        long timeLimit = (long) parameters.getOrDefault("time-limit", defaultTime);
        long iterationsLimit = (long) parameters.getOrDefault("iterations-limit", Long.MAX_VALUE);
        long iterationsPerTemperature = (long) parameters.getOrDefault("iterations-per-temperature", 1176628L);
        double initialTemperature = (double) parameters.getOrDefault("initial-temperature", 1.0);
        double coolingRate = (double) parameters.getOrDefault("cooling-rate", 0.96);

        // List of neighborhoods available
        List<Neighborhood> neighborhoods = Arrays.asList(
                new Shift(), new Switch(), new TaskMove(),
                new Swap(), new TwoShift(), new DirectSwap()
        );

        // Create a random solution as start solution
        RandomHeuristic randomHeuristic = new RandomHeuristic();
        Solution solution = randomHeuristic.solve(problem, random, null);

        // Make the initial solution as the incumbent one
        Solution bestSolution = new Solution(solution);

        // Notify callback about the initial solution
        callback.callback(new Solution(bestSolution), 0L, 0L);

        // Initialize the algorithm attributes
        long totalIterations = 0L;
        long iterationsInTemperature = 0L;
        double currentTemperature = initialTemperature;

        // Start timer
        Timer timer = new Timer(true);

        // Main loop: stop when time limit is reached or iterations limit is reached
        while (timer.count() < timeLimit && totalIterations < iterationsLimit) {

            // Update iteration counters
            ++totalIterations;
            ++iterationsInTemperature;

            // Use intensification policy?
            boolean intensification = random.nextBoolean();

            // Use makespan machine?
            int target = random.nextBoolean() ? solution.getMakespanMachine() : random.nextInt(problem.m);

            // Perform the move
            Neighborhood neighborhood = neighborhoods.get(random.nextInt(neighborhoods.size()));
            Solution trialSolution = null;
            if (intensification) {
                trialSolution = neighborhood.getBestNeighbor(problem, solution, target);
            } else {
                trialSolution = neighborhood.getAnyNeighbor(problem, solution, random, target);
            }

            // Check for improvement
            //int delta = trialSolution.getMakespan() - solution.getMakespan();
            int delta = trialSolution.getSumMachinesMakespan() - solution.getSumMachinesMakespan();
            if (delta <= 0) {

                // Accept the move
                solution = trialSolution;

                // Update the incumbent solution
                if (solution.getMakespan() < bestSolution.getMakespan()) {
                    bestSolution = new Solution(solution);

                    // Call callback function
                    if (callback != null) {

                        // Stop timer
                        timer.stop();

                        // Notify callback
                        callback.callback(new Solution(bestSolution), totalIterations, timer.count(TimeUnit.NANOSECONDS));

                        // Resume timer
                        timer.start();
                    }
                }

            } else {

                // May accept the move with a probability exp(-delta / currentTemperature)
                if (random.nextDouble() < Math.exp(-delta / currentTemperature)) {
                    solution = trialSolution;
                }
            }

            // Update temperature
            if (iterationsInTemperature >= iterationsPerTemperature) {
                iterationsInTemperature = 0L;
                currentTemperature = coolingRate * currentTemperature;

                // Reheating
                if (currentTemperature < EPS) {
                    currentTemperature = initialTemperature;
                }
            }
        }

        // Return the best solution found
        bestSolution.update();
        return bestSolution;
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Auxiliary classes

    /**
     * Callback class that allows custom process each time a new incumbent solution is found.
     */
    public interface Callback {

        /**
         * Callback function.
         *
         * @param incumbent The incumbent solution.
         * @param iteration The current iteration.
         * @param time      The current time (in nanoseconds).
         */
        void callback(Solution incumbent, long iteration, long time);
    }

}
