package br.ufmg.ppgee.orcslab.upmsp.cli.util;

import com.beust.jcommander.IStringConverter;

/**
 * Custom converter to parse parameters ({@link Param}).
 */
public class ParamConverter implements IStringConverter<Param> {

    @Override
    public Param convert(String value) {
        String[] s = value.split("=");
        return new Param(s[0], s[1]);
    }

}
