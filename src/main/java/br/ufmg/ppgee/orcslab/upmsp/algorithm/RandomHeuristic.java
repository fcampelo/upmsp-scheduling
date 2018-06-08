package br.ufmg.ppgee.orcslab.upmsp.algorithm;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.*;

/**
 * This heuristic creates a random solution for the unrelated parallel machine scheduling
 * problem with setup times dependent on the sequence and machine. The jobs are randomly
 * selected and assigned to a randomly selected machine.
 */
public class RandomHeuristic extends AbstractAlgorithm {

    @Override
    protected Solution doSolve(Problem problem, Random random, Map<String, Object> parameters, Callback callback) {

        // Create an empty solution
        Solution solution = new Solution(problem);

        // Create and shuffle a list with jobs
        List<Integer> jobs = new ArrayList<>(problem.n);
        for (int j = 0; j < problem.n; ++j) {
            jobs.add(j);
        }

        Collections.shuffle(jobs, random);

        // Schedule jobs
        for (int job : jobs) {

            // Choose a machine
            int k = random.nextInt(problem.m);

            // Assign job to machine
            solution.add(job, k, false);
        }

        // Update solution attibutes
        solution.update();

        // Return the solution
        return solution;
    }
}
