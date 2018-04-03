package br.ufmg.ppgee.orcslab.upmsp.problem;

/**
 * A solution to the unrelated parallel machine scheduling problem with setup times dependent on the sequence
 * and machine.
 */
public class Solution {

    private final Problem problem;
    private final Integer[] assignments;
    private final Integer[][] machines;
    private final int[] countJobs;
    private final int[] makespan;
    private int sumMachinesMakespan;
    private int idxMakespanMachine;
    private boolean updated;

    /**
     * Constructor.
     * @param problem An instance of the problem.
     */
    public Solution(final Problem problem) {
        this.problem = problem;
        this.assignments = new Integer[problem.n];
        this.machines = new Integer[problem.m][problem.n];
        this.countJobs = new int[problem.m];
        this.makespan = new int[problem.m];
        this.sumMachinesMakespan = 0;
        this.idxMakespanMachine = 0;
        this.updated = true;
    }

    /**
     * Copy constructor.
     * @param solution Solution to be copied.
     */
    public Solution(final Solution solution) {
        this(solution.problem);
        System.arraycopy(solution.assignments, 0, this.assignments, 0, problem.n);
        for (int k = 0; k < problem.m; ++k) {
            System.arraycopy(solution.machines[k], 0, this.machines[k], 0, problem.n);
        }
        System.arraycopy(solution.countJobs, 0, this.countJobs, 0, problem.m);
        System.arraycopy(solution.makespan, 0, this.makespan, 0, problem.m);
        this.sumMachinesMakespan = solution.sumMachinesMakespan;
        this.idxMakespanMachine = solution.idxMakespanMachine;
        this.updated = solution.updated;
    }

    /**
     * Return the job at the specified position of the machine.
     * @param k The machine.
     * @param position The position.
     * @return The job at the specified position.
     */
    public int get(int k, int position) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        assert position >= 0 && position < countJobs[k] : "Invalid position";
        return machines[k][position];
    }

    /**
     * Add a job at the end of the machine k.
     * @param job The job to add.
     * @param k The machine.
     * @param update If true, the solution attributes (e.g. makespan) are updated.
     */
    public void add(int job, int k, boolean update) {
        add(job, k, countJobs[k], update);
    }

    /**
     * Add a job at the specified position of the machine k.
     * @param job The job to add.
     * @param k The machine.
     * @param position Position at machine in which the job will be inserted.
     * @param update If true, the solution attributes (e.g. makespan) are updated.
     */
    public void add(int job, int k, int position, boolean update) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        assert job >= 0 && job < problem.n : "Invalid job";
        assert position >= 0 && position <= countJobs[k] : "Invalid position";

        // Update assignments
        assignments[job] = k;

        // Update makespan
        sumMachinesMakespan -= makespan[k];
        makespan[k] += getCostAdd(job, k, position);
        sumMachinesMakespan += makespan[k];

        // Shift task to the right
        System.arraycopy(machines[k], position, machines[k], position + 1, countJobs[k] - position);

        // Insert the job and update the counter
        machines[k][position] = job;
        ++countJobs[k];

        // Update, if required
        updated = false;
        if (update) {
            update();
        }
    }

    /**
     * Remove the job at the specified position of the machine k.
     * @param k The machine.
     * @param position Position of the job to remove from machine k.
     * @param update If true, the solution attributes (e.g. makespan) are updated.
     */
    public void remove(int k, int position, boolean update) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        assert position >= 0 && position < countJobs[k] : "Invalid position";

        // Update assignments
        assignments[machines[k][position]] = null;

        // Update makespan
        sumMachinesMakespan -= makespan[k];
        makespan[k] += getCostRemove(k, position);
        sumMachinesMakespan += makespan[k];

        // Shift task to the left
        System.arraycopy(machines[k], position + 1, machines[k], position, countJobs[k] - position - 1);

        // Update the counter
        --countJobs[k];

        // Update, if required
        updated = false;
        if (update) {
            update();
        }
    }

    /**
     * Set the job at the specified position of the machine k.
     * @param job The job.
     * @param k The machine.
     * @param position Position at machine that will be set.
     * @param update If true, the solution attributes (e.g. makespan) are updated.
     */
    public void set(int job, int k, int position, boolean update) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        assert job >= 0 && job < problem.n : "Invalid job";
        assert position >= 0 && position < countJobs[k] : "Invalid position";

        // Update assignments
        assignments[job] = k;

        // Update makespan
        sumMachinesMakespan -= makespan[k];
        makespan[k] += getCostSet(job, k, position);
        sumMachinesMakespan += makespan[k];

        // Change the job at the position
        machines[k][position] = job;

        // Update, if required
        updated = false;
        if (update) {
            update();
        }
    }

    /**
     * Return the number of jobs processed by the specified machine.
     * @param k The machine.
     * @return The number of jobs processed by the specified machine.
     */
    public int count(int k) {
        return countJobs[k];
    }

    /**
     * Return the machine to which the job was assigned. If the job is not assigned to any
     * machine, then null is returned.
     * @param job The job.
     * @return The machine to which the job was assigned.
     */
    public int getAssignment(int job) {
        assert job >= 0 && job < problem.n : "Invalid job";
        return assignments[job];
    }

    /**
     * Return the overall makespan of this solution (i.e., the completion time of the last job
     * processed). Note that the makespan may be outdated if the solution was modified and not
     * updated. To ensure that it is updated, call {@link #update()}.
     * @return The makespan of this solution.
     */
    public int getMakespan() {
        return makespan[idxMakespanMachine];
    }

    /**
     * Return the makespan of the specified machine (i.e., the completion time of the last job
     * processed by the machine).
     * @param k The machine.
     * @return The makespan of the machine
     */
    public int getMakespan(int k) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        return makespan[k];
    }

    /**
     * Return the sum of the makespan of all machines.
     * @return The sum of the makespan of all machines.
     */
    public int getSumMachinesMakespan() {
        return sumMachinesMakespan;
    }

    /**
     * Return the index of the makespan machine (i.e, the machine that process the last job
     * to be processed). Note that the makespan machine may be outdated if the solution was
     * modified and not updated. To ensure that it is updated, call {@link #update()}.
     * @return The index of the makespan machine.
     */
    public int getMakespanMachine() {
        return idxMakespanMachine;
    }

    /**
     * Return the change on the machines' makespan if a job is added in a specified position.
     * @param job The job to add.
     * @param k The machine.
     * @param position The position in which the job will be inserted.
     * @return The change in the machines' makespan.
     */
    public int getCostAdd(int job, int k, int position) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        assert job >= 0 && job < problem.n : "Invalid job";
        assert position >= 0 && position <= countJobs[k] : "Invalid position";

        if (countJobs[k] == 0) {
            return problem.p[k][job];
        } else if (position == 0) {
            return problem.s[k][job][machines[k][position]] + problem.p[k][job];
        } else if (position == countJobs[k]) {
            return problem.s[k][machines[k][position - 1]][job] + problem.p[k][job];
        } else {
            return -problem.s[k][machines[k][position - 1]][machines[k][position]]
                    + problem.s[k][machines[k][position - 1]][job]
                    + problem.s[k][job][machines[k][position]]
                    + problem.p[k][job];
        }
    }

    /**
     * Return the change on the machines' makespan if a job a the specified position is removed.
     * @param k The machine.
     * @param position The position of the job to remove.
     * @return The change in the machines' makespan.
     */
    public int getCostRemove(int k, int position) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        assert position >= 0 && position < countJobs[k] : "Invalid position";

        if (countJobs[k] == 1) {
            return -makespan[k];
        } else if (position == 0) {
            return -(problem.s[k][machines[k][position]][machines[k][position + 1]] + problem.p[k][machines[k][position]]);
        } else if (position == countJobs[k] - 1) {
            return -(problem.s[k][machines[k][position - 1]][machines[k][position]] + problem.p[k][machines[k][position]]);
        } else {
            return -(problem.s[k][machines[k][position - 1]][machines[k][position]] + problem.p[k][machines[k][position]] +
                    problem.s[k][machines[k][position]][machines[k][position + 1]]) +
                    problem.s[k][machines[k][position - 1]][machines[k][position + 1]];
        }
    }

    /**
     * Return the change in the machines' makespan if a job at a specified position is replaced by
     * another job.
     * @param job The job that will replace the current job at the specified position.
     * @param k The machine.
     * @param position The position of the job to replace.
     * @return The change in the machines' makespan.
     */
    public int getCostSet(int job, int k, int position) {
        assert k >= 0 && k < problem.m : "Invalid machine";
        assert job >= 0 && job < problem.n : "Invalid job";
        assert position >= 0 && position < countJobs[k] : "Invalid position";

        if (countJobs[k] == 1) {
            return -problem.p[k][machines[k][position]] + problem.p[k][job];
        } else if (position == 0) {
            return -(problem.s[k][machines[k][position]][machines[k][position + 1]] + problem.p[k][machines[k][position]])
                    + (problem.s[k][job][machines[k][position + 1]] + problem.p[k][job]);
        } else if (position == countJobs[k] - 1) {
            return -(problem.s[k][machines[k][position - 1]][machines[k][position]] + problem.p[k][machines[k][position]])
                    + (problem.s[k][machines[k][position - 1]][job] + problem.p[k][job]);
        } else {
            return -(problem.s[k][machines[k][position - 1]][machines[k][position]] + problem.p[k][machines[k][position]] +
                    problem.s[k][machines[k][position]][machines[k][position + 1]]) +
                    (problem.s[k][machines[k][position - 1]][job] + problem.p[k][job] +
                            problem.s[k][job][machines[k][position + 1]]);
        }
    }

    /**
     * Return true if this solution is feasible, or false otherwhise. Besides, the parameter
     * {@code builder} is different than null, message describing the infeasibility (or not)
     * is built.
     * @param builder A string builder.
     * @return True if this solution is feasible, of false otherwise.
     */
    public boolean isFeasible(StringBuilder builder) {

        // Check if all jobs was assigned only once
        int[] counter = new int[problem.n];
        for (int k = 0; k < problem.m; ++k) {
            for (int idx = 0; idx < countJobs[k]; ++idx) {

                // Check if the job is valid
                int job = machines[k][idx];
                if (job < 0 || job >= problem.n) {
                    if (builder != null) {
                        builder.append(String.format("Job %d is out of range [0, %d]", job, problem.n - 1));
                    }
                    return false;
                }
                ++counter[job];
            }
        }

        for (int job = 0; job < problem.n; ++job) {
            if (counter[job] == 0) {
                if (builder != null) {
                    builder.append(String.format("Job %d is not assigned to any machine.", job));
                }
                return false;
            } else if (counter[job] > 1) {
                if (builder != null) {
                    builder.append(String.format("Job %d is assigned to more then once.", job));
                }
                return false;
            }
        }

        // Check assignments
        for (int k = 0; k < problem.m; ++k) {
            for (int idx = 0; idx < countJobs[k]; ++idx) {
                int job = machines[k][idx];
                if (assignments[job] != k) {
                    if (builder != null) {
                        builder.append(String.format("Assignment of job %d is wrong. Found %d, expected %d", job, k, assignments[job]));
                    }
                    return false;
                }
            }
        }

        // Check makespan
        int idxMakespanMachine = 0;
        for (int k = 0; k < problem.m; ++k) {

            Integer[] machine = machines[k];
            int makespan = 0;

            if (countJobs[k] > 0) {
                makespan += problem.p[k][machine[0]];
                for (int idx = 1; idx < countJobs[k]; ++idx) {
                    int i = machine[idx - 1];
                    int j = machine[idx];
                    makespan += problem.s[k][i][j] + problem.p[k][j];
                }
            }

            if (makespan != this.makespan[k]) {
                if (builder != null) {
                    builder.append(String.format("Makespan is wrong in machine %d. Found %d, expected %d", k, makespan, this.makespan[k]));
                }
                return false;
            }

            if (makespan > this.makespan[idxMakespanMachine]) {
                idxMakespanMachine = k;
            }
        }

        // Check makespan machine
        if (idxMakespanMachine != this.idxMakespanMachine) {
            if (builder != null) {
                builder.append(String.format("Makespan machine is wrong. Found %d, expected %d", idxMakespanMachine, this.idxMakespanMachine));
            }
            return false;
        }

        // All constraints are satisfied
        if (builder != null) {
            builder.append("The solution is feasible");
        }
        return true;
    }

    /**
     * Update solution's attributes (e.g., makespan) if they have note been updated on the
     * last changes performed at this solution.
     */
    public void update() {
        if (!updated) {
            idxMakespanMachine = 0;
            for (int k = 1; k < problem.m; ++k) {
                if (makespan[k] > makespan[idxMakespanMachine]) {
                    idxMakespanMachine = k;
                }
            }
            updated = true;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int k = 0; k < problem.m; ++k) {
            builder.append("Machine " + k + ": [");
            if (countJobs[k] > 0) {
                for (int idx = 0; idx < countJobs[k] - 1; ++idx) {
                    builder.append("" + machines[k][idx] + ", ");
                }
                builder.append("" + machines[k][countJobs[k] - 1]);
            }
            builder.append("] (makespan: " + makespan[k] + ")\n");
        }

        return builder.toString();
    }

}
