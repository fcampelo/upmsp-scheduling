package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.Random;

/**
 * Switch neighborhood is defined by switching the order of two jobs on the same machine.
 * Considering a start solution with jobs equally distributed among the machines, the neighborhood
 * size is around O(n<sup>2</sup>).
 */
public class Switch extends AbstractNeighborhood {

    @Override
    public String getName() {
        return "Switch";
    }

    @Override
    public Solution getBestNeighbor(Problem problem, Solution solution) {

        // Create a copy of the start solution
        Solution base = new Solution(solution);
        base.update();

        // Track the best move
        boolean foundBest = false;
        int bestMakespan = base.getMakespan();
        int bestSumMachinesMakespan = base.getSumMachinesMakespan();
        int best_k = 0;
        int best_job1 = 0;
        int best_job2 = 0;
        int best_idx1 = 0;
        int best_idx2 = 0;

        // Evaluate all neighbors/moves
        for (int k = 0; k < problem.m; ++k) {
            if (base.count(k) > 1) {
                for (int idx1 = 0; idx1 < base.count(k); ++idx1) {
                    for (int idx2 = idx1 + 1; idx2 < base.count(k); ++idx2) {

                        // Evaluate the move
                        int job1 = base.get(k, idx1);
                        int job2 = base.get(k, idx2);
                        base.set(job1, k, idx2, false);
                        base.set(job2, k, idx1, false);

                        // Update the best move
                        base.update();
                        if (compare(base.getMakespan(), base.getSumMachinesMakespan(), bestMakespan, bestSumMachinesMakespan) < 0) {
                            foundBest = true;
                            bestMakespan = base.getMakespan();
                            bestSumMachinesMakespan = base.getSumMachinesMakespan();
                            best_k = k;
                            best_idx1 = idx1;
                            best_idx2 = idx2;
                            best_job1 = job1;
                            best_job2 = job2;
                        }

                        // Undo the move
                        base.set(job1, k, idx1, false);
                        base.set(job2, k, idx2, false);
                    }
                }
            }
        }

        // Perform the best move
        if (foundBest) {
            base.set(best_job1, best_k, best_idx2, false);
            base.set(best_job2, best_k, best_idx1, false);
        }

        base.update();
        return base;
    }

    @Override
    public Solution getAnyNeighbor(Problem problem, Solution solution, Random random) {

        // Create a copy of the start solution
        Solution base = new Solution(solution);
        base.update();

        // Perform move
        int k = random.nextInt(problem.m);
        while (base.count(k) < 2) {
            k = random.nextInt(problem.m);
        }

        int idx1 = random.nextInt(base.count(k));
        int idx2 = random.nextInt(base.count(k));
        while (idx2 == idx1) {
            idx2 = random.nextInt(base.count(k));
        }

        int job1 = base.get(k, idx1);
        int job2 = base.get(k, idx2);

        base.set(job1, k, idx2, false);
        base.set(job2, k, idx1, false);

        base.update();
        return base;
    }

    @Override
    public Stats getStats(Problem problem, Solution solution) {

        // Create a copy of the start solution
        solution.update();
        Solution base = new Solution(solution);

        // Stats
        Stats stats = new Stats(getName());

        // Evaluate all neighbors/moves
        for (int k = 0; k < problem.m; ++k) {
            if (base.count(k) > 1) {
                for (int idx1 = 0; idx1 < base.count(k); ++idx1) {
                    for (int idx2 = idx1 + 1; idx2 < base.count(k); ++idx2) {

                        // Evaluate the move
                        int job1 = base.get(k, idx1);
                        int job2 = base.get(k, idx2);
                        base.set(job1, k, idx2, false);
                        base.set(job2, k, idx1, false);

                        // Update stats
                        base.update();
                        // Update stats
                        stats.register(relation(base.getMakespan(), base.getSumMachinesMakespan(),
                                solution.getMakespan(), solution.getSumMachinesMakespan()));

                        // Undo the move
                        base.set(job1, k, idx1, false);
                        base.set(job2, k, idx2, false);
                    }
                }
            }
        }

        return stats;
    }
}
