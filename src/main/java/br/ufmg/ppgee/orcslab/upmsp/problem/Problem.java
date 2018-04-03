package br.ufmg.ppgee.orcslab.upmsp.problem;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * The unrelated parallel machine scheduling problem with setup times dependent on the sequence and machine.
 */
public class Problem {

    /**
     * Number of jobs.
     */
    public final int n;

    /**
     * Number of machines.
     */
    public final int m;

    /**
     * Processing times.
     */
    public final int[][] p;

    /**
     * Setup times.
     */
    public final int[][][] s;

    /**
     * Construction.
     *
     * @param filename Path to the instance file.
     * @throws IOException If an error occurs while reading the instance file.
     */
    public Problem(String filename) throws IOException {
        try (Scanner input = new Scanner(Paths.get(filename))) {

            // Read the size of the problem
            n = input.nextInt();
            m = input.nextInt();

            // Initialize the other attributes before reading
            p = new int[m][n];
            s = new int[m][n][n];

            // Skip next values
            input.nextInt();
            input.nextInt();

            // Read processing times
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k <m; ++k) {

                    // Skip next value (machine id)
                    input.nextInt();

                    // Read processing time
                    p[k][j] = input.nextInt();
                }
            }

            // Skip next value (SSD)
            input.next();

            // Read setup times
            for (int k = 0; k < m; ++k) {

                // Skip next value (machine id)
                input.next();

                // Read setup times on machine k
                for (int i = 0; i < n; ++i) {
                    for (int j = 0; j < n; ++j) {
                        s[k][i][j] = input.nextInt();
                    }
                }
            }
        }
    }

}
