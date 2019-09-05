package com.ifc.populationorderdeterminant.providers;

import com.ifc.populationorderdeterminant.app.App;

import java.util.*;

public class CommandOptionsProvider {

    public static final String CONFIG_FILE_PARAMETER = "-config";

    private static List<String> arguments;

    public static List<String> getArguments() {
        if (arguments == null) {
            synchronized (CommandOptionsProvider.class) {
                if (arguments == null) {
                    arguments = Arrays.asList(App.getArguments());
                }
            }
        }
        return arguments;
    }

    public static boolean hasOption(String option) {
        return getArguments().stream().anyMatch(argument -> argument.equalsIgnoreCase(option));
    }

    public static String valueOfRequired(String option) {
        return valueOf(option).orElseThrow(() -> new RuntimeException("Please, enter the " + option + " parameter."));
    }

    public static Optional<String> valueOf(String option) {
        for (int i = 0; i < getArguments().size(); i++) {

            String argument = getArguments().get(i);

            if (argument.equalsIgnoreCase(option)) {
                return Optional.of(getArguments().get(i + 1));
            }
        }
        return Optional.empty();
    }
}
