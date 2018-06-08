package br.ufmg.ppgee.orcslab.upmsp.algorithm;

import br.ufmg.ppgee.orcslab.upmsp.problem.Solution;

/**
 * Interface for callback.
 */
public interface Callback {

    /**
     * Called when a new incumbent solution is found.
     * @param incumbent The new incumbent solution.
     * @param iteration The current iteration.
     * @param time      The current time (in nanoseconds).
     */
    void onNewIncumbent(Solution incumbent, long iteration, long time);

}
