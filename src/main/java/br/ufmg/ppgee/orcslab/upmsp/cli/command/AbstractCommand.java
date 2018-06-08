package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Base class for command classes that already implements the logic to show the help message if
 * required by the user.
 */
public abstract class AbstractCommand implements Command {

    @Parameter(names = {"-h", "--help"}, help = true, description = "Show the help message of this command and exit.")
    public boolean help;

    @Override
    public void run(String name, JCommander cmd) throws Exception {

        // Show usage if required
        if (help) {
            cmd.usage(name);
            System.exit(0);
        }

        // Run the command
        doRun(name, cmd);
    }

    /**
     * This method must implement the logic to run the command.
     * @param name Name of this command in {@code cmd} object.
     * @param cmd Instance of JCommander used to parse the input arguments.
     * @throws Exception If any error occurs.
     */
    public abstract void doRun(String name, JCommander cmd) throws Exception;
}
