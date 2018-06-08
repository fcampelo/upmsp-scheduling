package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import br.ufmg.ppgee.orcslab.upmsp.algorithm.Callback;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.SimulatedAnnealing;
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

@Parameters(commandDescription = "Save all solutions which at some point were incumbents.")
public class TrackCommand extends AbstractCommand {

    @Parameter(names = "--verbose", description = "Show the progress.")
    public boolean verbose = false;

    @Parameter(names = "--threads", description = "Number of parallel threads to launch.")
    public Integer threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    @Parameter(names = "--repetitions", description = "Number of time the analysis is repeated.")
    public Integer repetitions = 1;

    @Parameter(names = "--instances-path", description = "Path to the directory with the instance files.", required = true)
    public String instancesPath = null;

    @Parameter(names = "--output-path", description = "Path to directory in which the data will be saved.")
    public String outputPath = ".";

    private long completedEntries;
    private long totalEntires;

    @Override
    public void doRun(String name, JCommander cmd) throws Exception {

        // List of instances to use
        File[] instances = Paths.get(instancesPath).toFile().listFiles((directory, filename) -> {
            return filename.endsWith(".txt");
        });

        // Create output directory, if it does not exists
        Files.createDirectories(Paths.get(outputPath));

        // Total number of entries to solve
        totalEntires = instances.length * repetitions;
        completedEntries = 0L;

        // Log
        if (verbose) {
            System.out.print(String.format("Progress: %d of %d (%.2f%%)", completedEntries, totalEntires,
                    100.0 * (completedEntries / (double) totalEntires)));
        }

        // Run entries
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath).resolve("summary.csv"))) {

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
        writer.append("INSTANCE,N,M,ID,SEED,TIME.LIMIT.NS,TIME.NS,ITERATION,INCUMBENT.MAX,INCUMBENT.SUM");
        writer.newLine();
        writer.flush();
    }

    private synchronized void writeSummary(BufferedWriter writer, Solution solution, String instance, long seed, long timeLimit, long time, long iteration) throws IOException {
        String[] dim = instance.trim().split("_");
        String data = String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d", instance, Integer.valueOf(dim[1]),
                Integer.valueOf(dim[2]), Integer.valueOf(dim[5]), seed, timeLimit, time, iteration,
                solution.getMakespan(), solution.getSumMachinesMakespan());
        writer.write(data);
        writer.newLine();
        writer.flush();
    }

    private void writeSolution(Problem problem, Solution solution, String instance, long seed, long iteration) throws IOException {

        // Create directory, if it does not exists
        Path path = Paths.get(outputPath).resolve("track").resolve(instance).resolve(String.valueOf(seed));
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

        TrackCommand launcher;
        BufferedWriter writer;
        private File instance;
        private long seed;

        public Runner(TrackCommand launcher, BufferedWriter writer, File instance, long seed) {
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
                long timeLimit = problem.n * (problem.m / 2) * 50;
                params.put("time-limit", timeLimit);
                params.put("iterations-limit", Long.MAX_VALUE);

                // Run the optimization algorithm
                CustomCallback callback = new CustomCallback();
                Random random = new Random(seed);
                algorithm.solve(problem, random, params, callback);

                // Write data
                for (Incumbent incumbent : callback.track) {
                    launcher.writeSummary(writer, incumbent.solution, instanceName, seed, TimeUnit.MILLISECONDS.toNanos(timeLimit), incumbent.time, incumbent.iteration);
                    launcher.writeSolution(problem, incumbent.solution, instanceName, seed, incumbent.iteration);
                }

                // Update progress
                launcher.onEntryCompleted();

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

}
