package br.ufmg.ppgee.orcslab.upmsp;

import br.ufmg.ppgee.orcslab.upmsp.cli.CommandLineInterface;

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
    }

}
