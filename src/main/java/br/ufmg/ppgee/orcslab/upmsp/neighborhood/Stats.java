package br.ufmg.ppgee.orcslab.upmsp.neighborhood;

/**
 * Statistics about a neighborhood.
 */
public class Stats {

    /**
     * Describe the relation between two solutions when comparing their quality.
     */
    public enum QualityRelation {
        BETTER_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN,
        BETTER_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN,
        BETTER_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN,
        EQUAL_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN,
        EQUAL_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN,
        EQUAL_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN,
        WORSE_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN,
        WORSE_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN,
        WORSE_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN;
    }


    private String neighborhood = null;
    private long nNeighbors = 0L;
    private long nBetterMakespanBetterSumMachinesMakespan = 0L;
    private long nBetterMakespanEqualSumMachinesMakespan = 0L;
    private long nBetterMakespanWorseSumMachinesMakespan = 0L;
    private long nWorseMakespanBetterSumMachinesMakespan = 0L;
    private long nWorseMakespanEqualSumMachinesMakespan = 0L;
    private long nWorseMakespanWorseSumMachinesMakespan = 0L;
    private long nEqualMakespanBetterSumMachinesMakespan = 0L;
    private long nEqualMakespanEqualSumMachinesMakespan = 0L;
    private long nEqualMakespanWorseSumMachinesMakespan = 0L;

    /**
     * Constructor.
     * @param neighborhood Name of the neighborhood from which the stats belong.
     */
    public Stats(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * Return the name of the neighborhood which generated this stats.
     * @return Name of the neighborhood which generated this stats.
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Register a new neighbor.
     * @param relation The relation of the neighbor with regarding to the start solution
     * used to define the neighborhood.
     */
    public void register(QualityRelation relation) {
        ++nNeighbors;
        switch (relation) {
            case BETTER_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN:
                ++nBetterMakespanBetterSumMachinesMakespan;
                break;

            case BETTER_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN:
                ++nBetterMakespanEqualSumMachinesMakespan;
                break;

            case BETTER_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN:
                ++nBetterMakespanWorseSumMachinesMakespan;
                break;

            case EQUAL_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN:
                ++nEqualMakespanBetterSumMachinesMakespan;
                break;

            case EQUAL_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN:
                ++nEqualMakespanEqualSumMachinesMakespan;
                break;

            case EQUAL_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN:
                ++nEqualMakespanWorseSumMachinesMakespan;
                break;

            case WORSE_MAKESPAN_BETTER_SUM_MACHINES_MAKESPAN:
                ++nWorseMakespanBetterSumMachinesMakespan;
                break;

            case WORSE_MAKESPAN_EQUAL_SUM_MACHINES_MAKESPAN:
                ++nWorseMakespanEqualSumMachinesMakespan;
                break;

            case WORSE_MAKESPAN_WORSE_SUM_MACHINES_MAKESPAN:
                ++nWorseMakespanWorseSumMachinesMakespan;
                break;
        }
    }

    /**
     * Return the number of neighbor solutions.
     */
    public long countNeighbors() {
        return nNeighbors;
    }

    /**
     * Return the number of neighbor solutions with better overall makespan.
     */
    public long countBetterMakespan() {
        return nBetterMakespanBetterSumMachinesMakespan +
                nBetterMakespanEqualSumMachinesMakespan +
                nBetterMakespanWorseSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better overall makespan.
     */
    public long countEqualMakespan() {
        return nEqualMakespanBetterSumMachinesMakespan +
                nEqualMakespanEqualSumMachinesMakespan +
                nEqualMakespanWorseSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better overall makespan.
     */
    public long countWorseMakespan() {
        return nWorseMakespanBetterSumMachinesMakespan +
                nWorseMakespanEqualSumMachinesMakespan +
                nWorseMakespanWorseSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better sum of machines' makespan.
     */
    public long countBetterSumMachinesMakespan() {
        return nBetterMakespanBetterSumMachinesMakespan +
                nEqualMakespanBetterSumMachinesMakespan +
                nWorseMakespanBetterSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better sum of machines' makespan.
     */
    public long countEqualSumMachinesMakespan() {
        return nBetterMakespanEqualSumMachinesMakespan +
                nEqualMakespanEqualSumMachinesMakespan +
                nWorseMakespanEqualSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better sum of machines' makespan.
     */
    public long countWorseSumMachinesMakespan() {
        return nBetterMakespanWorseSumMachinesMakespan +
                nEqualMakespanWorseSumMachinesMakespan +
                nWorseMakespanWorseSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better overall makespan and
     * better sum of machines' makespan.
     */
    public long countBetterMakespanBetterSumMachinesMakespan() {
        return nBetterMakespanBetterSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better overall makespan and
     * equal sum of machines' makespan.
     */
    public long countBetterMakespanEqualSumMachinesMakespan() {
        return nBetterMakespanEqualSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with better overall makespan and
     * worse sum of machines' makespan.
     */
    public long countBetterMakespanWorseSumMachinesMakespan() {
        return nBetterMakespanWorseSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with equal overall makespan and
     * better sum of machines' makespan.
     */
    public long countEqualMakespanBetterSumMachinesMakespan() {
        return nEqualMakespanBetterSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with equal overall makespan and
     * equal sum of machines' makespan.
     */
    public long countEqualMakespanEqualSumMachinesMakespan() {
        return nEqualMakespanEqualSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with equal overall makespan and
     * worse sum of machines' makespan.
     */
    public long countEqualMakespanWorseSumMachinesMakespan() {
        return nEqualMakespanWorseSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with worse overall makespan and
     * better sum of machines' makespan.
     */
    public long countWorseMakespanBetterSumMachinesMakespan() {
        return nWorseMakespanBetterSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with worse overall makespan and
     * equal sum of machines' makespan.
     */
    public long countWorseMakespanEqualSumMachinesMakespan() {
        return nWorseMakespanEqualSumMachinesMakespan;
    }

    /**
     * Return the number of neighbor solutions with worse overall makespan and
     * worse sum of machines' makespan.
     */
    public long countWorseMakespanWorseSumMachinesMakespan() {
        return nWorseMakespanWorseSumMachinesMakespan;
    }

    @Override
    public String toString() {

        // Number of digits to align the results
        int digits = String.valueOf(nNeighbors).length();
        int columnSize = Math.max("Number of Neighbors".length(), digits + " (000.00000%)".length());

        // Line
        int lineSize = " MAX(makespan[k]) | SUM(makespan[k]) | ".length() + columnSize + 1;
        StringBuilder lineBuilder = new StringBuilder();
        for (int i = 0; i < lineSize; ++i) {
            lineBuilder.append("-");
        }

        String line = lineBuilder.toString();

        // Build a string with the stats
        StringBuilder builder = new StringBuilder();
        builder.append(line + "\n");
        builder.append(String.format("%" + ((lineSize / 2) - (("Statistics - " + neighborhood).length() / 2)) + "s\n", "Statistics - " + neighborhood));
        builder.append(line + "\n");
        builder.append(String.format(" MAX(makespan[k]) | SUM(makespan[k]) | Number of Neighbors \n"));
        builder.append(line + "\n");
        builder.append(String.format("                  |                  | \n"));
        builder.append(String.format("                  | better           | %" + digits + "d (%9.5f%%)\n",
                nBetterMakespanBetterSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nBetterMakespanBetterSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format(" better           | equal            | %" + digits + "d (%9.5f%%)\n",
                nBetterMakespanEqualSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nBetterMakespanEqualSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format("                  | worse            | %" + digits + "d (%9.5f%%)\n",
                nBetterMakespanWorseSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nBetterMakespanWorseSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format("                  |                  | \n"));
        builder.append(line + "\n");
        builder.append(String.format("                  |                  | \n"));
        builder.append(String.format("                  | better           | %" + digits + "d (%9.5f%%)\n",
                nEqualMakespanBetterSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nEqualMakespanBetterSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format(" equal            | equal            | %" + digits + "d (%9.5f%%)\n",
                nEqualMakespanEqualSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nEqualMakespanEqualSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format("                  | worse            | %" + digits + "d (%9.5f%%)\n",
                nEqualMakespanWorseSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nEqualMakespanWorseSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format("                  |                  | \n"));
        builder.append(line + "\n");
        builder.append(String.format("                  |                  | \n"));
        builder.append(String.format("                  | better           | %" + digits + "d (%9.5f%%)\n",
                nWorseMakespanBetterSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nWorseMakespanBetterSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format(" worse            | equal            | %" + digits + "d (%9.5f%%)\n",
                nWorseMakespanEqualSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nWorseMakespanEqualSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format("                  | worse            | %" + digits + "d (%9.5f%%)\n",
                nWorseMakespanWorseSumMachinesMakespan, nNeighbors <= 0 ? 0 : 100.0 * (nWorseMakespanWorseSumMachinesMakespan / (double) nNeighbors)));
        builder.append(String.format("                  |                  | \n"));
        builder.append(line + "\n");
        builder.append(String.format("Neighborhood size: %d\n", nNeighbors));
        builder.append(line + "\n");

        return builder.toString();
    }
}