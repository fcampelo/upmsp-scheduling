package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import br.ufmg.ppgee.orcslab.upmsp.algorithm.Algorithm;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.Callback;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.SimulatedAnnealing;
import br.ufmg.ppgee.orcslab.upmsp.cli.util.Param;
import br.ufmg.ppgee.orcslab.upmsp.cli.util.ParamConverter;
import br.ufmg.ppgee.orcslab.upmsp.neighborhood.Neighborhood;
import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Parameters(commandDescription = "Solve an instance of the problem.")
public class OptimizeCommand extends AbstractCommand {

    @Parameter(names = "--verbose", description = "Print the progress of the algorithm throughout the optimization process.")
    public boolean verbose = false;

    @Parameter(names = "--print-solution", description = "Print the best solution found at the end of the optimization process")
    public boolean showSolution = false;

    @Parameter(names = "--seed", description = "Seed used to initialize the random number generator.")
    public Long seed = null;

    @Parameter(names = "--time-limit", description = "Total time for running the algorithm (in milliseconds).")
    public Long timeLimit = null;

    @Parameter(names = "--instance", description = "Path to the instance file.", required = true)
    public String instancePath = null;

    @Parameter(names = "--algorithm", description = "Algorithm used to solve the problem.", required = true)
    public String algorithmName = "simulated-annealing";

    @Parameter(names = "--param", description = "Algorithm parameters.", converter = ParamConverter.class)
    public List<Param> parameters = new ArrayList<>();

    @Override
    public void doRun(String name, JCommander cmd) throws Exception {

        // Load the instance file
        Problem problem = new Problem(Paths.get(instancePath));

        // Algorithm parameters
        Map<String, Object> params = new HashMap<>();

        // Initialize common algorithm parameters
        params.put("verbose", verbose);
        params.put("time-limit", timeLimit == null ? (long) problem.n * (problem.m / 2) * 50 : timeLimit);

        // Instantiate the algorithm and its specific parameters
        Algorithm algorithm = null;
        if ("simulated-annealing".equalsIgnoreCase(algorithmName)) {
            algorithm = new SimulatedAnnealing();
            proccessSAParameters(parameters, params);
        } else {
            throw new RuntimeException("Algorithm not found.");
        }

        // Initialize the random number generator
        Random random = (seed == null ? new Random() : new Random(seed));

        // Print log header
        if (verbose) {
            System.out.println("-------------------------------------------------------------");
            System.out.println("|    Iteration |        C_max |        C_sum |     Time (s) |");
            System.out.println("-------------------------------------------------------------");
        }

        // Solve the problem
        Solution solution = algorithm.solve(problem, random, params, new CustomCallback());
        solution.update();

        // Print log footer
        if (verbose) {
            System.out.println("-------------------------------------------------------------");
            System.out.println("Makespan (max): " + solution.getMakespan());
            System.out.println("Makespan (sum): " + solution.getSumMachinesMakespan());
            System.out.println("-------------------------------------------------------------");
            System.out.println();
        }

        for (Param param : parameters) {
            System.out.println("Parâmetro: " + param.name + " -> " + param.value);
        }

        // Print summary
        if (!verbose) {
            System.out.println(String.format("%d %d", solution.getMakespan(), solution.getSumMachinesMakespan()));
        }

        // Print solution
        if (showSolution) {
            System.out.println(solution);
        }

    }


    // --------------------------------------------------------------------------------------------
    // Auxiliary methods
    // --------------------------------------------------------------------------------------------

    private void proccessSAParameters(List<Param> input, Map<String, Object> output) {
        List<String> disabled = new ArrayList<>();
        output.put("disabled-neighborhoods", disabled);

        for (Param parameter : parameters) {
            switch (parameter.name) {

                case "iterations-limit":
                    output.put("iterations-limit", Long.parseLong(parameter.value));
                    break;

                case "iterations-per-temperature":
                    output.put("iterations-per-temperature", Long.parseLong(parameter.value));
                    break;

                case "initial-temperature":
                    output.put("initial-temperature", Long.parseLong(parameter.value));
                    break;

                case "cooling-rate":
                    output.put("cooling-rate", Long.parseLong(parameter.value));
                    break;

                case "disable":
                    disabled.add(parameter.value);
                    break;
            }
        }
    }


    // --------------------------------------------------------------------------------------------
    // Auxiliary classes
    // --------------------------------------------------------------------------------------------

    private static class CustomCallback implements Callback {

        @Override
        public void onNewIncumbent(Solution solution, long iteration, long time) {
            solution.update();
            System.out.println(String.format("| %12d | %12d | %12d | %12.3f |", iteration,
                    solution.getMakespan(), solution.getSumMachinesMakespan(), time / 1000000000.0));
        }
    }

}
