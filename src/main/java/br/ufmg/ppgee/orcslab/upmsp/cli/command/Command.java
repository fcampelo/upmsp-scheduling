package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import com.beust.jcommander.JCommander;

/**
 * Interface implemented by commnads of the command line interface (CLI).
 */
public interface Command {

    /**
     * Run the command.
     * @param name Name of this command in {@code cmd} object.
     * @param cmd Instance of JCommander used to parse the input arguments.
     * @throws Exception If any error occurs.
     */
    void run(String name, JCommander cmd) throws Exception;
}
