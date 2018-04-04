package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.Random;

/**
 * The Two-Shift neighborhood is defined by shifting the position of two jobs executed on te same
 * machine. Considering a start solution with jobs equally distributed among the machines, the
 * neighborhood size is around O(n<sup>4</sup>).
 */
public class TwoShift extends AbstractNeighborhood {

    @Override
    public String getName() {
        return "Two-Shift";
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
        int best_idx1_source = 0;
        int best_idx1_target = 0;
        int best_idx2_source = 0;
        int best_idx2_target = 0;

        // Evaluate all neighbors/moves
        for (int k = 0; k < problem.m; ++k) {
            if (base.count(k) > 1) {

                for (int idx1_source = 0; idx1_source < base.count(k); ++idx1_source) {
                    int job1 = base.get(k, idx1_source);
                    base.remove(k, idx1_source, false);

                    for (int idx2_source = idx1_source; idx2_source < base.count(k); ++idx2_source) {
                        int job2 = base.get(k, idx2_source);
                        base.remove(k, idx2_source, false);

                        for (int idx1_target = 0; idx1_target <= base.count(k); ++idx1_target) {
                            base.add(job1, k, idx1_target, false);

                            for (int idx2_target = 0; idx2_target <= base.count(k); ++idx2_target) {
                                base.add(job2, k, idx2_target, false);

                                // Update the best move
                                base.update();
                                if (compare(base.getMakespan(), base.getSumMachinesMakespan(), bestMakespan, bestSumMachinesMakespan) < 0) {
                                    foundBest = true;
                                    bestMakespan = base.getMakespan();
                                    bestSumMachinesMakespan = base.getSumMachinesMakespan();
                                    best_k = k;
                                    best_job1 = job1;
                                    best_job2 = job2;
                                    best_idx1_source = idx1_source;
                                    best_idx1_target = idx1_target;
                                    best_idx2_source = idx2_source;
                                    best_idx2_target = idx2_target;
                                }

                                // Undo the insertion
                                base.remove(k, idx2_target, false);
                            }

                            // Undo the insertion
                            base.remove(k, idx1_target, false);
                        }

                        // Undo removal
                        base.add(job2, k, idx2_source, false);
                    }

                    // Undo removal
                    base.add(job1, k, idx1_source, false);
                }
            }
        }

        // Perform the best move
        if (foundBest) {
            base.remove(best_k, best_idx1_source, false);
            base.remove(best_k, best_idx2_source, false);
            base.add(best_job1, best_k, best_idx1_target, false);
            base.add(best_job2, best_k, best_idx2_target, false);
        }

        base.update();
        return base;
    }

    @Override
    public Solution getAnyNeighbor(Problem problem, Solution solution, Random random) {

        // Create a copy of the start solution
        Solution base = new Solution(solution);
        base.update();

        // Perform a move
        int k = random.nextInt(problem.m);
        while (base.count(k) < 2) {
            k = random.nextInt(problem.m);
        }

        int idx1_source = random.nextInt(base.count(k));
        int job1 = base.get(k, idx1_source);
        base.remove(k, idx1_source, false);

        int idx2_source = random.nextInt(base.count(k));
        int job2 = base.get(k, idx2_source);
        base.remove(k, idx2_source, false);

        int idx1_target = random.nextInt(base.count(k) + 1);
        base.add(job1, k, idx1_target, false);

        int idx2_target = random.nextInt(base.count(k) + 1);
        base.add(job2, k, idx2_target, false);

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
        for (int k = 0; k < problem.m; ++k) {
            if (base.count(k) > 1) {

                for (int idx1_source = 0; idx1_source < base.count(k); ++idx1_source) {
                    int job1 = base.get(k, idx1_source);
                    base.remove(k, idx1_source, false);

                    for (int idx2_source = idx1_source; idx2_source < base.count(k); ++idx2_source) {
                        int job2 = base.get(k, idx2_source);
                        base.remove(k, idx2_source, false);

                        for (int idx1_target = 0; idx1_target <= base.count(k); ++idx1_target) {
                            base.add(job1, k, idx1_target, false);

                            for (int idx2_target = 0; idx2_target <= base.count(k); ++idx2_target) {
                                base.add(job2, k, idx2_target, false);

                                // Update stats
                                base.update();
                                stats.register(base);

                                // Undo the insertion
                                base.remove(k, idx2_target, false);
                            }

                            // Undo the insertion
                            base.remove(k, idx1_target, false);
                        }

                        // Undo removal
                        base.add(job2, k, idx2_source, false);
                    }

                    // Undo removal
                    base.add(job1, k, idx1_source, false);
                }
            }
        }

        return stats;
    }
}
