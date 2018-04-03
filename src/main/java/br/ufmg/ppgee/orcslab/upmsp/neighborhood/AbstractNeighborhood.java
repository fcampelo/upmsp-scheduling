package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

/**
 * Base class for neighborhoods that provides some useful methods when analysing a neighborhood.
 * Neighborhoods that extends this base class still have to implement the methods defined in
 * {@link Neighborhood}.
 */
public abstract class AbstractNeighborhood implements Neighborhood {

    /**
     * Return the quality relation of a first solution with regard to a second one.
     * @param firstMakespan The overall makespan of the first solution.
     * @param firstSumMakespan The sum of machines' makespan of the first solution.
     * @param secondMakespan The overall makespan of the second solution.
     * @param secondSumMakespan The sum of machines' makespan of the second solution.
     * @return The quality relation of the first solution with regard to the second one.
     */
    protected Stats.QualityRelation relation(int firstMakespan, int firstSumMakespan, int secondMakespan, int secondSumMakespan) {
        if (firstMakespan < secondMakespan) {
            if (firstSumMakespan < secondSumMakespan) {
                return Stats.QualityRelation.BETTER_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN;
            } else if (firstSumMakespan > secondSumMakespan) {
                return Stats.QualityRelation.BETTER_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN;
            } else {
                return Stats.QualityRelation.BETTER_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN;
            }

        } else if (firstMakespan > secondMakespan) {
            if (firstSumMakespan < secondSumMakespan) {
                return Stats.QualityRelation.WORSE_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN;
            } else if (firstSumMakespan > secondSumMakespan) {
                return Stats.QualityRelation.WORSE_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN;
            } else {
                return Stats.QualityRelation.WORSE_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN;
            }

        } else {
            if (firstSumMakespan < secondSumMakespan) {
                return Stats.QualityRelation.EQUAL_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN;
            } else if (firstSumMakespan > secondSumMakespan) {
                return Stats.QualityRelation.EQUAL_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN;
            } else {
                return Stats.QualityRelation.EQUAL_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN;
            }
        }
    }

    /**
     * Compare a first solution a a second one.
     * @param firstMakespan The overall makespan of the first solution.
     * @param firstSumMakespan The sum of machines' makespan of the first solution.
     * @param secondMakespan The overall makespan of the second solution.
     * @param secondSumMakespan The sum of machines' makespan of the second solution.
     * @return It returns -1, 0, or 1 if the first solution's quality is better, equal, or worse than the
     *      * second solution.
     */
    protected int compare(int firstMakespan, int firstSumMakespan, int secondMakespan, int secondSumMakespan) {
        if (firstMakespan < secondMakespan) {
            return -1;
        } else if (firstMakespan > secondMakespan) {
            return 1;
        } else {
            if (firstSumMakespan < secondSumMakespan) {
                return -1;
            } else if (firstSumMakespan > secondSumMakespan) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
