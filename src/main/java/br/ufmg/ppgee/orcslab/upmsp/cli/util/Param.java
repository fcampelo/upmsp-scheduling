package br.ufmg.ppgee.orcslab.upmsp.cli.util;

/**
 * Auxiliary class to store a parameter.
 */
public class Param {

    /**
     * Name of the parameter.
     */
    public final String name;

    /**
     * Value of the parameter.
     */
    public final String value;

    /**
     * Constructor.
     * @param name Name of the parameter.
     * @param value Valur of the parameter.
     */
    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
