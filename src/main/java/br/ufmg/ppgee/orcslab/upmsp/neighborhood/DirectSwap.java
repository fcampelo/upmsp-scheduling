package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.Random;

/**
 * The Direct Swap neighborhood is defined by swapping two jobs between two machines, maintaining
 * the previous positions on hese machines. Considering a start solution with jobs equally
 * distributed among the machines, the neighborhood size is around O(n<sup>2</sup>).
 */
public class DirectSwap extends AbstractNeighborhood {

    @Override
    public String getName() {
        return "Direct Swap";
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
        int best_idx1 = 0;
        int best_idx2 = 0;
        int best_job1 = 0;
        int best_job2 = 0;

        // Evaluate all neighbors/moves
        for (int k1 = 0; k1 < problem.m; ++k1) {
            if (base.count(k1) > 0) {

                for (int idx1 = 0; idx1 < base.count(k1); ++idx1) {
                    int job1 = base.get(k1, idx1);

                    for (int k2 = k1 + 1; k2 < problem.m; ++k2) {
                        if (base.count(k2) > 0) {

                            for (int idx2 = 0; idx2 < base.count(k2); ++idx2) {
                                int job2 = base.get(k2, idx2);

                                // Perform the move
                                base.set(job2, k1, idx1, false);
                                base.set(job1, k2, idx2, false);

                                // Update the best move
                                base.update();
                                if (compare(base.getMakespan(), base.getSumMachinesMakespan(), bestMakespan, bestSumMachinesMakespan) < 0) {
                                    foundBest = true;
                                    bestMakespan = base.getMakespan();
                                    bestSumMachinesMakespan = base.getSumMachinesMakespan();
                                    best_k1 = k1;
                                    best_k2 = k2;
                                    best_idx1 = idx1;
                                    best_idx2 = idx2;
                                    best_job1 = job1;
                                    best_job2 = job2;
                                }

                                // Undo the move
                                base.set(job2, k2, idx2, false);
                                base.set(job1, k1, idx1, false);
                            }
                        }
                    }
                }
            }
        }

        // Perform the best move
        if (foundBest) {
            base.set(best_job2, best_k1, best_idx1, false);
            base.set(best_job1, best_k2, best_idx2, false);
        }

        base.update();
        return base;
    }

    @Override
    public Solution getBestNeighbor(Problem problem, Solution solution, int target) {

        // Create a copy of the start solution
        Solution base = new Solution(solution);
        base.update();

        // Track the best move
        boolean foundBest = false;
        int bestMakespan = base.getMakespan();
        int bestSumMachinesMakespan = base.getSumMachinesMakespan();
        int best_k1 = 0;
        int best_k2 = 0;
        int best_idx1 = 0;
        int best_idx2 = 0;
        int best_job1 = 0;
        int best_job2 = 0;

        // Evaluate all neighbors/moves
        int k1 = target;
        if (base.count(k1) > 0) {

            for (int idx1 = 0; idx1 < base.count(k1); ++idx1) {
                int job1 = base.get(k1, idx1);

                for (int k2 = 0; k2 < problem.m; ++k2) {
                    if (k2 != k1 && base.count(k2) > 0) {

                        for (int idx2 = 0; idx2 < base.count(k2); ++idx2) {
                            int job2 = base.get(k2, idx2);

                            // Perform the move
                            base.set(job2, k1, idx1, false);
                            base.set(job1, k2, idx2, false);

                            // Update the best move
                            base.update();
                            if (compare(base.getMakespan(), base.getSumMachinesMakespan(), bestMakespan, bestSumMachinesMakespan) < 0) {
                                foundBest = true;
                                bestMakespan = base.getMakespan();
                                bestSumMachinesMakespan = base.getSumMachinesMakespan();
                                best_k1 = k1;
                                best_k2 = k2;
                                best_idx1 = idx1;
                                best_idx2 = idx2;
                                best_job1 = job1;
                                best_job2 = job2;
                            }

                            // Undo the move
                            base.set(job2, k2, idx2, false);
                            base.set(job1, k1, idx1, false);
                        }
                    }
                }
            }
        }

        // Perform the best move
        if (foundBest) {
            base.set(best_job2, best_k1, best_idx1, false);
            base.set(best_job1, best_k2, best_idx2, false);
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
        int k1 = random.nextInt(problem.m);
        while (base.count(k1) < 1) {
            k1 = random.nextInt(problem.m);
        }

        int idx1 = random.nextInt(base.count(k1));
        int job1 = base.get(k1, idx1);

        int k2 = random.nextInt(problem.m);
        while (k2 == k1 || base.count(k2) < 1) {
            k2 = random.nextInt(problem.m);
        }

        int idx2 = random.nextInt(base.count(k2));
        int job2 = base.get(k2, idx2);

        base.set(job2, k1, idx1, false);
        base.set(job1, k2, idx2, false);

        base.update();
        return base;
    }

    @Override
    public Solution getAnyNeighbor(Problem problem, Solution solution, Random random, int target) {

        // Create a copy of the start solution
        Solution base = new Solution(solution);
        base.update();

        // Perform a move
        int k1 = target;
        if (base.count(k1) < 1) {
            return base;
        }

        int idx1 = random.nextInt(base.count(k1));
        int job1 = base.get(k1, idx1);

        int k2 = random.nextInt(problem.m);
        while (k2 == k1 || base.count(k2) < 1) {
            k2 = random.nextInt(problem.m);
        }

        int idx2 = random.nextInt(base.count(k2));
        int job2 = base.get(k2, idx2);

        base.set(job2, k1, idx1, false);
        base.set(job1, k2, idx2, false);

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
            if (base.count(k1) > 0) {

                for (int idx1 = 0; idx1 < base.count(k1); ++idx1) {
                    int job1 = base.get(k1, idx1);

                    for (int k2 = k1 + 1; k2 < problem.m; ++k2) {
                        if (base.count(k2) > 0) {

                            for (int idx2 = 0; idx2 < base.count(k2); ++idx2) {
                                int job2 = base.get(k2, idx2);

                                // Perform the move
                                base.set(job2, k1, idx1, false);
                                base.set(job1, k2, idx2, false);

                                // Update stats
                                base.update();
                                stats.register(base);

                                // Undo the move
                                base.set(job2, k2, idx2, false);
                                base.set(job1, k1, idx1, false);
                            }
                        }
                    }
                }
            }
        }

        return stats;
    }
}
