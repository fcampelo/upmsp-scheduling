package br.ufmg.ppgee.orcslab.upmsp.cli;

import br.ufmg.ppgee.orcslab.upmsp.cli.command.AnalyzeCommand;
import br.ufmg.ppgee.orcslab.upmsp.cli.command.Command;
import br.ufmg.ppgee.orcslab.upmsp.cli.command.OptimizeCommand;
import br.ufmg.ppgee.orcslab.upmsp.cli.command.TrackCommand;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Command line interface.
 */
@Parameters(commandDescription = "Unrelated Parallel Machine Scheduling Problem with Sequence Dependent Setup Times")
public class CommandLineInterface {

    @Parameter(names = {"-h", "--help"}, help = true, description = "Show the help message and exit.")
    public boolean help;

    private Map<String, Command> commands;

    /**
     * Constructor.
     */
    public CommandLineInterface() {
        commands = new HashMap<>();
        commands.put("optimize", new OptimizeCommand());
        commands.put("track", new TrackCommand());
        commands.put("analyze", new AnalyzeCommand());
    }

    /**
     * Run the program from its input arguments.
     * @throws Exception If any error occurs.
     */
    public void run(String[] args) {

        // Initialize the parser
        JCommander cmd = JCommander.newBuilder()
                .addObject(this)
                .build();

        // Add commands
        for (String name : commands.keySet()) {
            cmd.addCommand(name, commands.get(name));
        }

        try {

            // Parse input arguments
            cmd.parse(args);

            // Show usage if required
            if (help) {
                cmd.usage();
                System.exit(0);
            }

            // Run the parsed command
            String command = cmd.getParsedCommand();
            commands.get(command).run(command, cmd);

        } catch (ParameterException e) {

            // Get usage
            StringBuilder str = new StringBuilder();
            cmd.usage(str);

            // Show error and usage
            System.err.println("ERROR: " + e.getMessage());
            System.err.println(str.toString());

        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
