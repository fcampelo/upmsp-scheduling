package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistics about a neighborhood.
 */
public class Stats {

    /**
     * A type of entry.
     */
    public enum Type {
        BETTER_BETTER, BETTER_EQUAL, BETTER_WORSE,
        EQUAL_BETTER, EQUAL_EQUAL, EQUAL_WORSE,
        WORSE_BETTER, WORSE_EQUAL, WORSE_WORSE;
    }

    /**
     * Keep stats of a type of entry.
     */
    private static class Entry {
        public long count = 0L;
        public long accDeltaMakespan = 0L;
        public long bestDeltaMakespan = 0L;
        public long worstDeltaMakespan = 0L;
        public long accDeltaSumMachinesMakespan = 0L;
        public long bestDeltaSumMachinesMakespan = Long.MAX_VALUE;
        public long worstDeltaSumMachinesMakespan = Long.MIN_VALUE;
    }


    // Class attributes
    private String neighborhood = null;         // Name of the neighborhood
    private long refMakespan = 0L;              // Makespan of the reference solution
    private long refSumMachinesMakespan = 0L;   // Sum of machines' makespan of the reference solution
    private long nNeighbors = 0L;               // Number of neighbor solutions
    Map<Type, Entry> entries;                   // List with a entry for each type of relation neighbor/reference solutions

    /**
     * Constructor.
     * @param neighborhood The neighborhood.
     * @param ref Start solution used to generate the neighborhood.
     */
    public Stats(Neighborhood neighborhood, Solution ref) {
        ref.update();
        this.neighborhood = neighborhood.getName();
        this.refMakespan = ref.getMakespan();
        this.refSumMachinesMakespan = ref.getSumMachinesMakespan();

        entries = new HashMap<>();
        for (Type type : Type.values()) {
            entries.put(type, new Entry());
        }
    }

    /**
     * Return the name of the neighborhood which generated this stats.
     * @return Name of the neighborhood which generated this stats.
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Register a neighbor.
     * @param neighbor A neighbor solution.
     */
    public void register(Solution neighbor) {

        // Increment the neighbors counter
        ++nNeighbors;

        // Make sure the neighbor solution status is updated
        neighbor.update();

        // Get the type of relation between the neighbor and reference solution
        Type type = type(neighbor.getMakespan(), neighbor.getSumMachinesMakespan());

        // Update stats
        Entry entry = entries.get(type);
        ++entry.count;

        long deltaMakespan = neighbor.getMakespan() - refMakespan;
        entry.accDeltaMakespan += deltaMakespan;
        entry.bestDeltaMakespan = Math.min(entry.bestDeltaMakespan, deltaMakespan);
        entry.worstDeltaMakespan = Math.max(entry.worstDeltaMakespan, deltaMakespan);

        long deltaSumMachinesMakespan = neighbor.getSumMachinesMakespan() - refSumMachinesMakespan;
        entry.accDeltaSumMachinesMakespan += deltaSumMachinesMakespan;
        entry.bestDeltaSumMachinesMakespan = Math.min(entry.bestDeltaSumMachinesMakespan, deltaSumMachinesMakespan);
        entry.worstDeltaSumMachinesMakespan = Math.max(entry.worstDeltaSumMachinesMakespan, deltaSumMachinesMakespan);
    }

    /**
     * Return the number of neighbor solutions.
     */
    public long countNeighbors() {
        return nNeighbors;
    }

    /**
     * Return the number of neighbor solutions of specified type.
     * @param type The type of relation.
     * @return The number of neighbor solutions.
     */
    public long countNeighbors(Type type) {
        return entries.get(type).count;
    }

    /**
     * Return the better change on the overall makespan with regard to neighbor solutions
     * of the specified type.
     * @param type The type of relation.
     * @return The better change on the overall makespan.
     *
     */
    public long bestDeltaMakespan(Type type) {
        return entries.get(type).bestDeltaMakespan;
    }

    /**
     * Return the worst change on the overall makespan with regard to neighbor solutions
     * of the specified type.
     * @param type The type of relation.
     * @return The worst change on the overall makespan.
     *
     */
    public long worstDeltaMakespan(Type type) {
        return entries.get(type).worstDeltaMakespan;
    }

    /**
     * Return the mean change on the overall makespan with regard to neighbor solutions
     * of the specified type.
     * @param type The type of relation.
     * @return The mean change on the overall makespan.
     *
     */
    public double meanDeltaMakespan(Type type) {
        if (entries.get(type).count > 0) {
            return entries.get(type).accDeltaMakespan / (double) entries.get(type).count;
        }
        return 0.0;
    }

    /**
     * Return the better change on the sum of machines' makespan with regard to neighbor
     * solutions of the specified type.
     * @param type The type of relation.
     * @return The better change on the sum of machines' makespan.
     *
     */
    public long bestDeltaSumMachinesMakespan(Type type) {
        return entries.get(type).bestDeltaSumMachinesMakespan;
    }

    /**
     * Return the worst change on the sum of machines' makespan with regard to neighbor
     * solutions of the specified type.
     * @param type The type of relation.
     * @return The worst change on the sum of machines' makespan.
     *
     */
    public long worstDeltaSumMachinesMakespan(Type type) {
        return entries.get(type).worstDeltaSumMachinesMakespan;
    }

    /**
     * Return the mean change on the sum of machines' makespan with regard to neighbor
     * solutions of the specified type.
     * @param type The type of relation.
     * @return The mean change on the sum of machines' makespan.
     *
     */
    public double meanDeltaSumMachinesMakespan(Type type) {
        if (entries.get(type).count > 0) {
            return entries.get(type).accDeltaSumMachinesMakespan / (double) entries.get(type).count;
        }
        return 0.0;
    }

    /**
     * Return the relation of the neighbor solution to the reference solution.
     * @param makespan The overall makespan of the first solution.
     * @param sumMachinesMakespan The sum of machines' makespan of the first solution.
     * @return The relation of the neighbor solution to the reference solution.
     */
    private Type type(int makespan, int sumMachinesMakespan) {
        if (makespan < refMakespan) {
            if (sumMachinesMakespan < refSumMachinesMakespan) {
                return Type.BETTER_BETTER;
            } else if (sumMachinesMakespan > refSumMachinesMakespan) {
                return Type.BETTER_WORSE;
            } else {
                return Type.BETTER_EQUAL;
            }

        } else if (makespan > refMakespan) {
            if (sumMachinesMakespan < refSumMachinesMakespan) {
                return Type.WORSE_BETTER;
            } else if (sumMachinesMakespan > refSumMachinesMakespan) {
                return Type.WORSE_WORSE;
            } else {
                return Type.WORSE_EQUAL;
            }

        } else {
            if (sumMachinesMakespan < refSumMachinesMakespan) {
                return Type.EQUAL_BETTER;
            } else if (sumMachinesMakespan > refSumMachinesMakespan) {
                return Type.EQUAL_WORSE;
            } else {
                return Type.EQUAL_EQUAL;
            }
        }
    }

    @Override
    public String toString() {

        // Number of digits to align columns
        int col1 = "MAX(makespan)".length();
        int col2 = "SUM(makespan)".length();
        int col3 = Math.max("ALL (%)".length(), "000.0000".length());
        int col4 = Math.max("GROUP (%)".length(), "000.0000".length());

        int col5 = "DELTA (best)".length();
        int col6 = "DELTA (worst)".length();
        int col7 = "DELTA (mean)".length();

        for (Type type : entries.keySet()) {
            col5 = Math.max(col5, String.valueOf(entries.get(type).bestDeltaSumMachinesMakespan).length());
            col6 = Math.max(col6, String.valueOf(entries.get(type).worstDeltaSumMachinesMakespan).length());
            col7 = Math.max(col7, col6 + 3);
        }

        // Separation line
        int length = col1 + col2 + col3 + col4 + col5 + col6 + col7 + 20;
        StringBuilder sepBuilder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sepBuilder.append("-");
        }

        sepBuilder.append("\n");
        String sep = sepBuilder.toString();

        // Keep content
        StringBuilder content = new StringBuilder();

        // Patterns
        String patternHeader = String.format(" %%%ds | %%%ds | %%%ds | %%%ds | %%%ds | %%%ds | %%%ds \n",
                col1, col2, col3, col4, col5, col6, col7);

        String patternEntry = String.format(" %%%ds | %%%ds | %%%d.4f | %%%d.4f | %%%dd | %%%dd | %%%d.2f \n",
                col1, col2, col3, col4, col5, col6, col7);

        // Size of groups
        long countBetter = entries.get(Type.BETTER_BETTER).count + entries.get(Type.BETTER_EQUAL).count +
                entries.get(Type.BETTER_WORSE).count;
        countBetter = Math.max(countBetter, 1L);

        long countEqual = entries.get(Type.EQUAL_BETTER).count + entries.get(Type.EQUAL_EQUAL).count +
                entries.get(Type.EQUAL_WORSE).count;
        countEqual = Math.max(countEqual, 1L);

        long countWorse = entries.get(Type.WORSE_BETTER).count + entries.get(Type.WORSE_EQUAL).count +
                entries.get(Type.WORSE_WORSE).count;
        countWorse = Math.max(countWorse, 1L);

        // Header
        content.append(sep);
        content.append(String.format("Neighborhood: %s\n", neighborhood));
        content.append(sep);
        content.append(String.format(patternHeader,
                "MAX(makespan)", "SUM(makespan)", "ALL (%)", "GROUP (%)",
                "DELTA (best)", "DELTA (worst)", "DELTA (mean)"));

        // Better makespan
        content.append(sep);

        content.append(String.format(patternEntry, "", "better",
                100.0 * entries.get(Type.BETTER_BETTER).count / (double) nNeighbors,
                100.0 * entries.get(Type.BETTER_BETTER).count / (double) countBetter,
                entries.get(Type.BETTER_BETTER).bestDeltaSumMachinesMakespan,
                entries.get(Type.BETTER_BETTER).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.BETTER_BETTER)));

        content.append(String.format(patternEntry, "better", "equal",
                100.0 * entries.get(Type.BETTER_EQUAL).count / (double) nNeighbors,
                100.0 * entries.get(Type.BETTER_EQUAL).count / (double) countBetter,
                entries.get(Type.BETTER_EQUAL).bestDeltaSumMachinesMakespan,
                entries.get(Type.BETTER_EQUAL).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.BETTER_EQUAL)));

        content.append(String.format(patternEntry, "", "worse",
                100.0 * entries.get(Type.BETTER_WORSE).count / (double) nNeighbors,
                100.0 * entries.get(Type.BETTER_WORSE).count / (double) countBetter,
                entries.get(Type.BETTER_WORSE).bestDeltaSumMachinesMakespan,
                entries.get(Type.BETTER_WORSE).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.BETTER_WORSE)));

        // Equal makespan
        content.append(sep);

        content.append(String.format(patternEntry, "", "better",
                100.0 * entries.get(Type.EQUAL_BETTER).count / (double) nNeighbors,
                100.0 * entries.get(Type.EQUAL_BETTER).count / (double) countEqual,
                entries.get(Type.EQUAL_BETTER).bestDeltaSumMachinesMakespan,
                entries.get(Type.EQUAL_BETTER).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.EQUAL_BETTER)));

        content.append(String.format(patternEntry, "equal", "equal",
                100.0 * entries.get(Type.EQUAL_EQUAL).count / (double) nNeighbors,
                100.0 * entries.get(Type.EQUAL_EQUAL).count / (double) countEqual,
                entries.get(Type.EQUAL_EQUAL).bestDeltaSumMachinesMakespan,
                entries.get(Type.EQUAL_EQUAL).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.EQUAL_EQUAL)));

        content.append(String.format(patternEntry, "", "worse",
                100.0 * entries.get(Type.EQUAL_WORSE).count / (double) nNeighbors,
                100.0 * entries.get(Type.EQUAL_WORSE).count / (double) countEqual,
                entries.get(Type.EQUAL_WORSE).bestDeltaSumMachinesMakespan,
                entries.get(Type.EQUAL_WORSE).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.EQUAL_WORSE)));

        // Worse makespan
        content.append(sep);

        content.append(String.format(patternEntry, "", "better",
                100.0 * entries.get(Type.WORSE_BETTER).count / (double) nNeighbors,
                100.0 * entries.get(Type.WORSE_BETTER).count / (double) countWorse,
                entries.get(Type.WORSE_BETTER).bestDeltaSumMachinesMakespan,
                entries.get(Type.WORSE_BETTER).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.WORSE_BETTER)));

        content.append(String.format(patternEntry, "worse", "equal",
                100.0 * entries.get(Type.WORSE_EQUAL).count / (double) nNeighbors,
                100.0 * entries.get(Type.WORSE_EQUAL).count / (double) countWorse,
                entries.get(Type.WORSE_EQUAL).bestDeltaSumMachinesMakespan,
                entries.get(Type.WORSE_EQUAL).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.WORSE_EQUAL)));

        content.append(String.format(patternEntry, "", "worse",
                100.0 * entries.get(Type.WORSE_WORSE).count / (double) nNeighbors,
                100.0 * entries.get(Type.WORSE_WORSE).count / (double) countWorse,
                entries.get(Type.WORSE_WORSE).bestDeltaSumMachinesMakespan,
                entries.get(Type.WORSE_WORSE).worstDeltaSumMachinesMakespan,
                meanDeltaSumMachinesMakespan(Type.WORSE_WORSE)));

        // Footer
        content.append(sep);
        content.append(String.format("Neighborhood size: %d\n", nNeighbors));
        content.append(sep);

        return content.toString();
    }
}