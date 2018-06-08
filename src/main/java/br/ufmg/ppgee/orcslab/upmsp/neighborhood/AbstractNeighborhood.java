package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

/**
 * Base class for neighborhoods that provides some useful methods when analysing a neighborhood.
 * Neighborhoods that extends this base class still have to implement the methods defined in
 * {@link Neighborhood}.
 */
public abstract class AbstractNeighborhood implements Neighborhood {

    /**
     * Compare a first solution a a second one.
     * @param firstMakespan The overall makespan of the first solution.
     * @param firstSumMachinesMakespan The sum of machines' makespan of the first solution.
     * @param secondMakespan The overall makespan of the second solution.
     * @param secondSumMachinesMakespan The sum of machines' makespan of the second solution.
     * @return It returns -1, 0, or 1 if the first solution's quality is better, equal, or worse than the
     * second solution.
     */
    protected int compare(int firstMakespan, int firstSumMachinesMakespan, int secondMakespan, int secondSumMachinesMakespan) {
        if (firstMakespan < secondMakespan) {
            return -1;
        } else if (firstMakespan > secondMakespan) {
            return 1;
        } else {
            if (firstSumMachinesMakespan < secondSumMachinesMakespan) {
                return -1;
            } else if (firstSumMachinesMakespan > secondSumMachinesMakespan) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
