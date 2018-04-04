package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.Random;

/**
 * Task Move neighborhood is defined by moving a job from its current machine to another machine.
 * Considering a start solution with jobs equally distributed among the machines, the neighborhood
 * size is around O(n<sup>2</sup>).
 */
public class TaskMove extends AbstractNeighborhood {

    @Override
    public String getName() {
        return "Task Move";
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
        int best_k1 = 0;
        int best_k2 = 0;
        int best_job = 0;
        int best_idx1 = 0;
        int best_idx2 = 0;

        // Evaluate all neighbors/moves
        for (int k1 = 0; k1 < problem.m; ++k1) {
            for (int idx1 = 0; idx1 < base.count(k1); ++idx1) {
                int job = base.get(k1, idx1);
                base.remove(k1, idx1, false);

                for (int k2 = 0; k2 < problem.m; ++k2){
                    if (k2 != k1) {
                        for (int idx2 = 0; idx2 <= base.count(k2); ++idx2) {
                            base.add(job, k2, idx2, false);

                            // Update the best move
                            base.update();
                            if (compare(base.getMakespan(), base.getSumMachinesMakespan(), bestMakespan, bestSumMachinesMakespan) < 0) {
                                foundBest = true;
                                bestMakespan = base.getMakespan();
                                bestSumMachinesMakespan = base.getSumMachinesMakespan();
                                best_k1 = k1;
                                best_k2 = k2;
                                best_job = job;
                                best_idx1 = idx1;
                                best_idx2 = idx2;
                            }

                            // Undo the insertion of job at position idx2
                            base.remove(k2, idx2, false);
                        }
                    }
                }

                // Undo the removal of job from position idx1
                base.add(job, k1, idx1, false);
            }
        }

        // Perform the best move
        if (foundBest) {
            base.remove(best_k1, best_idx1, false);
            base.add(best_job, best_k2, best_idx2, false);
        }

        base.update();
        return base;
    }

    @Override
    public Solution getAnyNeighbor(Problem problem, Solution solution, Random random) {

        // Create a copy of the start solution
        Solution base = new Solution(solution);
        base.update();

        // Perform the move
        int k1 = random.nextInt(problem.m);
        while (base.count(k1) < 1) {
            k1 = random.nextInt(problem.m);
        }

        int idx1 = random.nextInt(base.count(k1));
        int job = base.get(k1, idx1);
        base.remove(k1, idx1, false);

        int k2 = random.nextInt(problem.m);
        while (k2 == k1) {
            k2 = random.nextInt(problem.m);
        }

        int idx2 = random.nextInt(base.count(k2) + 1);
        base.add(job, k2, idx2, false);

        base.update();
        return base;
    }

    @Override
    public Stats getStats(Problem problem, Solution solution) {

        // Create a copy of the start solution
        solution.update();
        Solution base = new Solution(solution);

        // Stats
        Stats stats = new Stats(this, solution);

        // Evaluate all neighbors/moves
        for (int k1 = 0; k1 < problem.m; ++k1) {
            for (int idx1 = 0; idx1 < base.count(k1); ++idx1) {
                int job = base.get(k1, idx1);
                base.remove(k1, idx1, false);

                for (int k2 = 0; k2 < problem.m; ++k2){
                    if (k2 != k1) {
                        for (int idx2 = 0; idx2 <= base.count(k2); ++idx2) {
                            base.add(job, k2, idx2, false);

                            // Update stats
                            base.update();
                            stats.register(base);

                            // Undo the insertion of job at position idx2
                            base.remove(k2, idx2, false);
                        }
                    }
                }

                // Undo the removal of job from position idx1
                base.add(job, k1, idx1, false);
            }
        }

        return stats;
    }
}
