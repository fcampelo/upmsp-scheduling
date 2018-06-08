package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import br.ufmg.ppgee.orcslab.upmsp.algorithm.Callback;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.SimulatedAnnealing;
import br.ufmg.ppgee.orcslab.upmsp.neighborhood.*;
import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Parameters(commandDescription = "Perform the neighborhood analysis.")
public class AnalyzeCommand extends AbstractCommand {

    @Parameter(names = "--verbose", description = "Show the progress.")
    public boolean verbose = false;

    @Parameter(names = "--threads", description = "Number of threads used to perform the analysis.")
    public Integer threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    @Parameter(names = "--optimize", description = "Perform the analysis throughout the optimization process.")
    public boolean optimize = false;

    @Parameter(names = "--track", description = "Save all solutions which at some point were incumbents.")
    public boolean track = false;

    @Parameter(names = "--repetitions", description = "Number of times the analysis will be repeated.")
    public Integer repetitions = 1;

    @Parameter(names = "--instances", description = "Path to the directory containing the instance files.", required = true)
    public String instancesPath = null;

    @Parameter(names = "--output", description = "Path to the file in which the data will be saved.")
    public String output = ".";

    private long completedEntries;
    private long totalEntires;
    private List<Neighborhood> neighborhoods;

    @Override
    public void doRun(String name, JCommander cmd) throws Exception {

        // List of instances to use
        File[] instances = Paths.get(instancesPath).toFile().listFiles((directory, filename) -> {
            return filename.endsWith(".txt");
        });

        // List of neighborhoods to perform the analysis
        neighborhoods = Arrays.asList(
                new Shift(), new Switch(), new TaskMove(),
                new Swap(), new DirectSwap(), new TwoShift()
        );

        // Total number of entries to solve
        totalEntires = instances.length * neighborhoods.size() * repetitions;
        completedEntries = 0L;

        // Log
        if (verbose) {
            System.out.print(String.format("Progress: %d of %d (%.2f%%)", completedEntries, totalEntires,
                    100.0 * (completedEntries / (double) totalEntires)));
        }

        // Run entries
        Files.createDirectories(Paths.get(output).getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(output))) {

            // Write header of CSV file
            writeHeader(writer);

            ExecutorService executor = Executors.newFixedThreadPool(threads);
            for (int repetition = 1; repetition <= repetitions; ++repetition) {
                for (File instance : instances) {
                    Runner entry = new Runner(this, writer, instance, repetition);
                    executor.execute(entry);
                }
            }

            /// Wait all threads to finish
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }

        System.out.println();
    }

    private void writeHeader(BufferedWriter writer) throws IOException {
        writer.append("INSTANCE,N,M,ID,SEED,TIME.LIMIT.NS,TIME.NS,ITERATION,INCUMBENT.MAX,INCUMBENT.SUM,NEIGHBORHOOD," +
                "NEIGHBORHOOD.SIZE,CLASS.MAX,CLASS.SUM,COUNT,MAX.BEST,MAX.WORST,MAX.MEAN,SUM.BEST,SUM.WORST,SUM.MEAN");
        writer.newLine();
        writer.flush();
    }

    private synchronized void writeSummary(BufferedWriter writer, Solution solution, Stats stats, String instance,
                                           String neighborhood, Stats.Type type, long seed, long timeLimit, long time, long iteration) throws IOException {

        // Some data
        String[] dim = instance.trim().split("_");
        String[] clazz = type.name().toLowerCase().split("_");

        // Prepare data for writing
        String data = String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%s,%d,%s,%s,%d,%d,%d,%.6f,%d,%d,%.6f",
                instance, Integer.valueOf(dim[1]), Integer.valueOf(dim[2]), Integer.valueOf(dim[5]), seed, timeLimit,
                time, iteration, solution.getMakespan(), solution.getSumMachinesMakespan(), neighborhood,
                stats.countNeighbors(), clazz[0], clazz[1], stats.countNeighbors(type), stats.bestDeltaMakespan(type),
                stats.worstDeltaMakespan(type), stats.meanDeltaMakespan(type), stats.bestDeltaSumMachinesMakespan(type),
                stats.worstDeltaSumMachinesMakespan(type), stats.meanDeltaSumMachinesMakespan(type));

        // Write data
        writer.write(data);
        writer.newLine();
        writer.flush();
    }

    private void writeSolution(Problem problem, Solution solution, String instance, long seed, long iteration) throws IOException {

        // Create directory, if it does not exists
        Path path = Paths.get(output).getParent().resolve("track").resolve(instance).resolve(String.valueOf(seed));
        synchronized(this) {
            Files.createDirectories(path);
        }

        // Write file
        Path file = path.resolve(String.format("iteration_%d.txt", iteration));
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (int k = 0; k < problem.m; ++k) {
                for (int idx = 0; idx < solution.count(k); ++idx) {
                    writer.write(String.format("%d ", solution.get(k, idx)));
                }
                writer.newLine();
            }
        }
    }

    private synchronized void onEntryCompleted() {
        ++completedEntries;
        if (verbose) {
            System.out.print(String.format("\rProgress: %d of %d (%.2f%%)", completedEntries, totalEntires,
                    100.0 * (completedEntries / (double) totalEntires)));
        }
    }


    // --------------------------------------------------------------------------------------------
    // Callback classes
    // --------------------------------------------------------------------------------------------

    private static class Incumbent {

        public Solution solution;
        public long iteration;
        public long time;

        public Incumbent(Solution solution, long iteration, long time) {
            this.solution = solution;
            this.iteration = iteration;
            this.time = time;
        }
    }

    private static class CustomCallback implements Callback {

        public List<Incumbent> track = new LinkedList<>();

        @Override
        public void onNewIncumbent(Solution solution, long iteration, long time) {
            track.add(new Incumbent(solution, iteration, time));
        }
    }

    private static class Runner implements Runnable {

        AnalyzeCommand launcher;
        BufferedWriter writer;
        private File instance;
        private long seed;

        public Runner(AnalyzeCommand launcher, BufferedWriter writer, File instance, long seed) {
            this.launcher = launcher;
            this.writer = writer;
            this.instance = instance;
            this.seed = seed;
        }

        @Override
        public void run() {
            try {

                // Load the instance file
                String instanceName = instance.getName().replace(".txt", "");
                Problem problem = new Problem(instance.toPath());

                // Instantiate the algorithm (Simulated Annealing)
                SimulatedAnnealing algorithm = new SimulatedAnnealing();

                // Algorithm parameters
                Map<String, Object> params = new HashMap<>();

                long timeLimit = 0L;
                if (launcher.optimize) {
                    timeLimit = problem.n * (problem.m / 2) * 50;
                    params.put("time-limit", timeLimit);
                    params.put("iterations-limit", Long.MAX_VALUE);
                } else {
                    params.put("time-limit", timeLimit);
                    params.put("iterations-limit", 0L);
                }

                // Run the optimization algorithm
                CustomCallback callback = new CustomCallback();
                Random random = new Random(seed);
                algorithm.solve(problem, random, params, callback);

                // Write data
                for (Neighborhood neighborhood : launcher.neighborhoods) {
                    for (Incumbent incumbent : callback.track) {

                        // Evaluate the neighborhood
                        Stats stats = neighborhood.getStats(problem, incumbent.solution);

                        // Write the summary
                        for (Stats.Type type : Stats.Type.values()) {
                            launcher.writeSummary(writer, incumbent.solution, stats, instanceName, neighborhood.getName(),
                                    type, seed, TimeUnit.MILLISECONDS.toNanos(timeLimit), incumbent.time, incumbent.iteration);
                        }

                        // Write solution
                        if (launcher.track) {
                            launcher.writeSolution(problem, incumbent.solution, instanceName, seed, incumbent.iteration);
                        }
                    }

                    // Update progress
                    launcher.onEntryCompleted();
                }

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

}
