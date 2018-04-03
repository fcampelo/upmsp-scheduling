package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Perform the analysis of the neighborhoods.")
public class AnalizeCommand extends AbstractCommand {

    @Parameter(names = "--neighborhood", description = "A neighborhood to evaluate.")
    public List<String> neighborhoods = new ArrayList<>();

    @Parameter(names = "--verbose", description = "Print the result of the analysis.")
    public boolean vebose = false;

    @Parameter(names = "--optimize", description = "Perform the analysis throught an optimization process.")
    public boolean optimize = false;

    @Parameter(names = "--instances", description = "Path to the directory with the instance files.")
    public String instances = ".";

    @Override
    public void doRun(String name, JCommander cmd) throws Exception {
        // TODO
    }
}
