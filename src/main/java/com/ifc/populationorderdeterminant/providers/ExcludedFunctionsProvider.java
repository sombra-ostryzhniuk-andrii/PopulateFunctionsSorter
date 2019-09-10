package com.ifc.populationorderdeterminant.providers;

import com.ifc.populationorderdeterminant.entity.Function;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcludedFunctionsProvider {

    private static Set<Function> configExcludedFunctions;
    private static final Set<Function> runtimeExcludedFunctions = new HashSet<>();

    private static Set<Function> loadConfigExcludedFunctions() {
        return PropertiesProvider.getSchemas()
                .stream()
                .map(schema -> PropertiesProvider.getExcludedFunctionsSet(schema.getName()))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public static Set<Function> getConfigExcludedFunctions() {
        if (configExcludedFunctions == null) {
            synchronized (ExcludedFunctionsProvider.class) {
                if (configExcludedFunctions == null) {
                    configExcludedFunctions = loadConfigExcludedFunctions();
                }
            }
        }
        return configExcludedFunctions;
    }

    public static Set<Function> getRuntimeExcludedFunctions() {
        return new HashSet<>(runtimeExcludedFunctions);
    }

    public static Set<Function> getConfigExcludedFunctionsBySchema(String schema) {
        return getConfigExcludedFunctions()
                .stream()
                .filter(function -> function.getSchema().equals(schema))
                .collect(Collectors.toSet());
    }

    public static Set<Function> getRuntimeExcludedFunctionsBySchema(String schema) {
        return runtimeExcludedFunctions
                .stream()
                .filter(function -> function.getSchema().equals(schema))
                .collect(Collectors.toSet());
    }

    public static Set<Function> getAllExcludedFunctions() {
        return Stream.of(getConfigExcludedFunctions(), runtimeExcludedFunctions)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public static void addRuntimeExcludedFunction(Function function) {
        runtimeExcludedFunctions.add(function);
    }

    public static void addRuntimeExcludedFunction(Function function, String logMessage) {
        if (!runtimeExcludedFunctions.contains(function)) {
            addRuntimeExcludedFunction(function);
            System.err.println("The function " + function + " is excluded, the result may be incorrect. " + logMessage);
        }
    }

    public static boolean isFunctionExcluded(Function function) {
        return getAllExcludedFunctions()
                .stream()
                .anyMatch(excludedFunction -> Objects.equals(excludedFunction, function));
    }
}
