package br.ufmg.ppgee.orcslab.upmsp.util;

import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

/**
 * Methods used to compare solutions.
 */
public class Comparator implements java.util.Comparator<Solution> {

    /**
     * Compare two solutions.
     * @param solution1 The first solution.
     * @param solution2 The second solution.
     * @return It returns -1, 0, or 1 if the first solution's quality is better, equal, or worse than the
     * second solution.
     */
    public int compare(Solution solution1, Solution solution2) {

        // Make sure the solutions' attributes are updated
        solution1.update();
        solution2.update();

        if (solution1.getMakespan() < solution2.getMakespan()) {
            return -1;
        } else if (solution1.getMakespan() > solution2.getMakespan()) {
            return 1;
        } else {
            if (solution1.getSumMachinesMakespan() < solution2.getSumMachinesMakespan()) {
                return -1;
            } else if (solution1.getSumMachinesMakespan() > solution2.getSumMachinesMakespan()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
