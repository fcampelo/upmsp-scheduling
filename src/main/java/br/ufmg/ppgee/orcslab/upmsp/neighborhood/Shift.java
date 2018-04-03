package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.Random;

/**
 * Shift neighborhood is defined by rescheduling a job from a machine to another position on the
 * same machine. Considering a start solution with jobs equally distributed among the machines,
 * the neighborhood size is around O(n<sup>2</sup>).
 */
public class Shift extends AbstractNeighborhood {

    @Override
    public String getName() {
        return "Shift";
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
        int best_job = 0;
        int best_idx1 = 0;
        int best_idx2 = 0;

        // Evaluate all neighbors/moves
        for (int k = 0; k < problem.m; ++k) {
            if (base.count(k) > 1) {

                for (int idx1 = 0; idx1 < base.count(k); ++idx1) {
                    int job = base.get(k, idx1);
                    base.remove(k, idx1, false);

                    for (int idx2 = 0; idx2 <= base.count(k); ++idx2) {
                        if (idx2 != idx1) {
                            base.add(job, k, idx2, false);

                            // Update the best move
                            base.update();
                            if (compare(base.getMakespan(), base.getSumMachinesMakespan(), bestMakespan, bestSumMachinesMakespan) < 0) {
                                foundBest = true;
                                bestMakespan = base.getMakespan();
                                bestSumMachinesMakespan = base.getSumMachinesMakespan();
                                best_k = k;
                                best_job = job;
                                best_idx1 = idx1;
                                best_idx2 = idx2;
                            }

                            // Undo the insertion of job at position idx2
                            base.remove(k, idx2, false);
                        }
                    }

                    // Undo the removal of job from position idx1
                    base.add(job, k, idx1, false);
                }
            }
        }

        // Perform the best move
        if (foundBest) {
            base.remove(best_k, best_idx1, false);
            base.add(best_job, best_k, best_idx2, false);
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
        int k = random.nextInt(problem.m);
        while (base.count(k) < 2) {
            k = random.nextInt(problem.m);
        }

        int idx1 = random.nextInt(base.count(k));
        int job = base.get(k, idx1);
        base.remove(k, idx1, false);

        int idx2 = random.nextInt(base.count(k) + 1);
        while (idx2 == idx1) {
            idx2 = random.nextInt(base.count(k) + 1);
        }

        base.add(job, k, idx2, true);

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
                    int job = base.get(k, idx1);
                    base.remove(k, idx1, false);

                    for (int idx2 = 0; idx2 <= base.count(k); ++idx2) {
                        if (idx2 != idx1) {
                            base.add(job, k, idx2, false);

                            // Update stats
                            base.update();
                            stats.register(relation(base.getMakespan(), base.getSumMachinesMakespan(),
                                    solution.getMakespan(), solution.getSumMachinesMakespan()));

                            // Undo the insertion of job at position idx2
                            base.remove(k, idx2, false);
                        }
                    }

                    // Undo the removal of job from position idx1
                    base.add(job, k, idx1, false);
                }
            }
        }

        return stats;
    }
}
