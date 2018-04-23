package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import br.ufmg.ppgee.orcslab.upmsp.algorithm.Algorithm;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.RandomHeuristic;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.SimulatedAnnealing;
import br.ufmg.ppgee.orcslab.upmsp.neighborhood.*;
import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Parameters(commandDescription = "Perform an analysis of the neighborhoods.")
public class AnalizeCommand extends AbstractCommand {

    @Parameter(names = "--verbose", description = "Show the progress of the analysis.")
    public boolean verbose = false;

    @Parameter(names = "--optimize", description = "Perform the analysis throught an optimization process.")
    public boolean optimize = false;

    @Parameter(names = "--repetitions", description = "Perform the analysis throught an optimization process.")
    public Integer repetitions = 1;

    @Parameter(names = "--instances-path", description = "Path to the directory with the instance files.")
    public String instancesPath = ".";

    @Parameter(names = "--output-file", description = "Path to file in which the data will be saved.")
    public String outputPath = "analysis-data.csv";

    @Override
    public void doRun(String name, JCommander cmd) throws Exception {
        if (optimize) {
            runWithOptimization();
        } else {
            runWithoutOptimization();
        }
    }

    /**
     * Perform the analysis of the neighborhoods with randomly generated solutions.
     * @throws Exception If some error occur.
     */
    private void runWithoutOptimization() throws Exception {

        // List of neighborhoods
        List<Neighborhood> neighborhoods = Arrays.asList(
                new Shift(),
                new Switch(),
                new TaskMove(),
                new Swap(),
                new DirectSwap(),
                new TwoShift()
        );

        // List of instances to use
        File[] instances = Paths.get(instancesPath).toFile().listFiles((directory, filename) -> {
            return filename.endsWith(".txt");
        });

        // Calculate the total of entries to be analyzed
        long total = instances.length * neighborhoods.size() * repetitions;

        // Algorithm used to generate reference solutions
        Algorithm algorithm = new RandomHeuristic();

        // Perform the analysis
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {

            // Write header
            writeHeader(writer);

            long completed = 0L;
            for (int repetition = 1; repetition <= repetitions; ++repetition) {
                for (File instance : instances) {

                    // Load the instance file
                    String instanceName = instance.getName().replace(".txt", "");
                    Problem problem = new Problem(instance.toPath());

                    // Create a reference solution
                    Random random = new Random(repetition);
                    Solution solution = algorithm.solve(problem, random, null);

                    for (Neighborhood neighborhood : neighborhoods) {

                        // Evaluate the neighborhood
                        Stats stats = neighborhood.getStats(problem, solution);

                        // Write data
                        write(writer, instanceName, neighborhood.getName(), repetition, 0, 0, solution, stats);

                        // Print progress
                        ++completed;
                        if (verbose) {
                            System.out.print(String.format("\rProgress: %d of %d (%.2f%%)", completed, total,
                                    100.0 * (completed / (double) total)));
                        }
                    }
                }
            }

            if (verbose) {
                System.out.println();
            }
        }
    }

    /**
     * Peform the analysis of the neighborhoods throughout an optimization algorithm. The algorithm
     * used for optimization is {@link SimulatedAnnealing}.
     * @throws Exception If some error occur.
     */
    private void runWithOptimization() throws Exception {

        // List of neighborhoods
        List<Neighborhood> neighborhoods = Arrays.asList(
                new Shift(),
                new Switch(),
                new TaskMove(),
                new Swap(),
                new DirectSwap(),
                new TwoShift()
        );

        // List of instances to use
        File[] instances = Paths.get(instancesPath).toFile().listFiles((directory, filename) -> {
            return filename.endsWith(".txt");
        });

        // Calculate the total of entries to be analyzed
        long total = instances.length * neighborhoods.size() * repetitions;

        // Algorithm used to generate reference solutions
        SimulatedAnnealing algorithm = new SimulatedAnnealing();

        // Perform the analysis
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {

            // Write header
            writeHeader(writer);

            long completed = 0L;
            for (int repetition = 1; repetition <= repetitions; ++repetition) {
                for (File instance : instances) {

                    // Load the instance file
                    String instanceName = instance.getName().replace(".txt", "");
                    Problem problem = new Problem(instance.toPath());

                    // Algorithm parameters
                    Map<String, Object> params = new HashMap<>();
                    long timeLimit = problem.n * (problem.m / 2) * 50;
                    params.put("time-limit", timeLimit);

                    // Run the optimization algorithm
                    Callback callback = new Callback();
                    Random random = new Random(repetition);
                    algorithm.solve(problem, random, params, callback);

                    for (Neighborhood neighborhood : neighborhoods) {
                        for (Entry entry : callback.entries) {

                            // Evaluate the neighborhood
                            Stats stats = neighborhood.getStats(problem, entry.solution);

                            // Write data
                            write(writer, instanceName, neighborhood.getName(), repetition, timeLimit, entry.time, entry.solution, stats);
                        }

                        // Print progress
                        ++completed;
                        if (verbose) {
                            System.out.print(String.format("\rProgress: %d of %d (%.2f%%)", completed, total,
                                    100.0 * (completed / (double) total)));
                        }
                    }
                }
            }

            if (verbose) {
                System.out.println();
            }
        }
    }

    private void writeHeader(BufferedWriter writer) throws IOException {
        writer.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                "INSTANCE", "SEED", "TIME", "INCUMBENT.MAX", "INCUMBENT.SUM", "NEIGHBORHOOD", "NEIGHBORHOOD.SIZE",
                "CLASS.MAX", "CLASS.SUM", "COUNT", "MAX.BEST", "MAX.WORST", "MAX.MEAN", "SUM.BEST", "SUM.WORST", "SUM.MEAN"));
        writer.newLine();
    }

    private void write(BufferedWriter writer, String instance, String neighborhood, long seed, long timeLimit, long time, Solution solution, Stats stats) throws IOException {

        // Write each entry
        for (Stats.Type type : Stats.Type.values()) {
            write(writer, instance, neighborhood, seed, timeLimit, time, solution, stats, type);
        }

        // Flush data
        writer.flush();
    }

    private void write(BufferedWriter writer, String instance, String neighborhood, long seed, long timeLimit, long time, Solution solution, Stats stats, Stats.Type type) throws IOException {

        String[] strType = type.name().toLowerCase().split("_");

        // Prepare data for writing
        String data = String.format("%s,%d,%d,%d,%d,%d,%s,%d,%s,%s,%d,%d,%d,%.6f,%d,%d,%.6f",
                instance, seed, timeLimit, time, solution.getMakespan(), solution.getSumMachinesMakespan(), neighborhood,
                stats.countNeighbors(), strType[0], strType[1], stats.countNeighbors(type),
                stats.bestDeltaMakespan(type),
                stats.worstDeltaMakespan(type),
                stats.meanDeltaMakespan(type),
                stats.bestDeltaSumMachinesMakespan(type),
                stats.worstDeltaSumMachinesMakespan(type),
                stats.meanDeltaSumMachinesMakespan(type));

        // Write data
        writer.write(data);
        writer.newLine();
    }


    // --------------------------------------------------------------------------------------------
    // Callback classes
    // --------------------------------------------------------------------------------------------

    private static class Entry {

        public Solution solution;
        public long iteration;
        public long time;

        public Entry(Solution solution, long iteration, long time) {
            this.solution = solution;
            this.iteration = iteration;
            this.time = time;
        }
    }

    private static class Callback implements SimulatedAnnealing.Callback {

        public List<Entry> entries = new LinkedList<>();

        @Override
        public void callback(Solution incumbent, long iteration, long time) {
            entries.add(new Entry(incumbent, iteration, time));
        }
    }
}
