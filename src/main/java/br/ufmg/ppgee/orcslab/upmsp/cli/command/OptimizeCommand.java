package br.ufmg.ppgee.orcslab.upmsp.cli.command;

import br.ufmg.ppgee.orcslab.upmsp.cli.util.ParamConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Solve an instance of the problem.")
public class OptimizeCommand extends AbstractCommand {

    @Parameter(names = "-verbose", description = "Print the progress of the algorithm throughout the optimization process.")
    public boolean vebose = false;

    @Parameter(names = "-seed", description = "Seed used to initialize the random number generator.")
    public Long seed = null;

    @Parameter(names = "-instance", description = "Path to the instance file.", required = true)
    public String instance = null;

    @Parameter(names = "-algorithm", description = "Algorithm used to solve the problem.", required = true)
    public String algorithm = null;

    @Parameter(names = "-param", description = "Algorithm parameters.", converter = ParamConverter.class)
    public List<String> parameters = new ArrayList<>();

    @Override
    public void doRun(String name, JCommander cmd) throws Exception {
        // TODO
    }
}
