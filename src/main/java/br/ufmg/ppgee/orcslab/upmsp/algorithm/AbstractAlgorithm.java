package br.ufmg.ppgee.orcslab.upmsp.algorithm;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Base class for algorithms that process the input parameters to avoid {@code null} values for the
 * random number generator and for the map of algorithm parameters. Algorithms that extends this
 * base class must override the protected method {@link #doSolve(Problem, Random, Map, Callback)},
 * in which the algorithm's logic will be placed.
 */
public abstract class AbstractAlgorithm implements Algorithm {

    @Override
    public final Solution solve(Problem problem, Random random, Map<String, Object> parameters, Callback callback) {

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

        // Callback object
        if (callback == null) {
            callback = new Callback() {
                @Override
                public void onNewIncumbent(Solution incumbent, long iteration, long time) { /* Do nothing. */ }
            };
        }

        // Solve the problem
        return doSolve(problem, random, parameters, callback);
    }

    /**
     * Method that must implement the algorithm's logic that solves the input problem and returns a
     * solution.
     * @param problem The problem instance.
     * @param random A random number generator.
     * @param parameters Algorithm parameters.
     * @param callback A callback object.
     * @return A solution of the problem.
     */
    protected abstract Solution doSolve(Problem problem, Random random, Map<String, Object> parameters, Callback callback);

}
