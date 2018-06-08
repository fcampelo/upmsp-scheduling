package br.ufmg.ppgee.orcslab.upmsp.algorithm;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.Map;
import java.util.Random;

/**
 * Common interface implemented by all algorithms that solves the unrelated parallel
 * machine scheduling problem with setup times dependent on the sequence and machine.
 */
public interface Algorithm {

    /**
     * Solve the problem and return the solution found.
     * @param problem Instance of the problem to be solved.
     * @param random A random number generator.
     * @param parameters Algorithm parameters.
     * @param callback A callback object.
     * @return A solution to the problem.
     */
    Solution solve(Problem problem, Random random, Map<String, Object> parameters, Callback callback);

}
