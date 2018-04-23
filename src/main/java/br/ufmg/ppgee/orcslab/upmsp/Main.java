package br.ufmg.ppgee.orcslab.upmsp;

import br.ufmg.ppgee.orcslab.upmsp.algorithm.Algorithm;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.RandomHeuristic;
import br.ufmg.ppgee.orcslab.upmsp.algorithm.SimulatedAnnealing;
import br.ufmg.ppgee.orcslab.upmsp.cli.CommandLineInterface;
import br.ufmg.ppgee.orcslab.upmsp.neighborhood.*;
import br.ufmg.ppgee.orcslab.upmsp.problem.Problem;
import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Main class with the entry point ({@link #main(String[])}) of this program.
 */
public class Main {

    /**
     * Entry point of this program.
     * @param args Input arguments of the program.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CommandLineInterface cli = new CommandLineInterface();
        cli.run(args);

//        // Load problem
//        String filename = "/home/andre/Downloads/RSDST/RSDST/small/I_6_2_S_1-9_1.txt";
//        //String filename = "/home/andre/Downloads/RSDST/RSDST/large/I_250_30_S_1-124_10.txt";
//        Problem problem = new Problem(Paths.get(filename));
//
//        // Create a solution
//        SimulatedAnnealing algorithm = new SimulatedAnnealing();
//        Solution solution = algorithm.solve(problem, new Random(System.currentTimeMillis()), null,
//                new SimulatedAnnealing.Callback() {
//                    @Override
//                    public void callback(Solution incumbent, long iteration, long time) {
//                        System.out.println(String.format("%12d -> %12d", iteration, incumbent.getMakespan()));
//                    }
//                });
//
//        System.out.print(solution);
//
//        // Check feasibility
//        StringBuilder builder = new StringBuilder();
//        solution.isFeasible(builder);
//        System.out.println("Feasibility: " + builder.toString());

//        // Create a random solution
//        Algorithm algorithm = new RandomHeuristic();
//        Solution solution = algorithm.solve(problem, new Random(0), null);
//        System.out.print(solution);
//
//        // Check feasibility
//        StringBuilder builder = new StringBuilder();
//        solution.isFeasible(builder);
//        System.out.println("Feasibility: " + builder.toString());
//
//        // Evaluate neighborhoods
//        System.out.println("==============================================================================");
//        List<Neighborhood> neighborhoods = Arrays.asList(
//                new Shift(),
//                new Switch(),
//                new TaskMove(),
//                new Swap(),
//                new DirectSwap(),
//                new TwoShift()
//        );
//
//        for (Neighborhood neighborhood : neighborhoods) {
//            Stats stats = neighborhood.getStats(problem, solution);
//            System.out.println(stats);
//            System.out.println();
//        }
//        System.out.println("==============================================================================");
    }

}
