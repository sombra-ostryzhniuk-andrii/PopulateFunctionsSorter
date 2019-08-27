package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.app.PropertiesProvider;
import com.ifc.populatefunctionssorter.entity.Function;
import com.ifc.populatefunctionssorter.repository.FunctionsDAO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionService {

    private List<String> excludedFunctions;

    public FunctionService() {
        excludedFunctions = getExcludedFunctions();
    }

    public List<Function> getAllPopulateFunctionsInSchema(String schema) {
        return FunctionsDAO.getAllPopulateFunctionsInSchema(schema).stream()
                .filter(function -> !isFunctionExcluded(function.getName()))
                .peek(function -> function.setSchema(schema))
                .collect(Collectors.toList());
    }

    public String getViewNameByFunction(Function function) {
        return StringUtils.substringBetween(
                function.getDefinition(),
                "from " + function.getSchema() + ".",
                "\n");
    }

    public String getTableNameByFunction(Function function) {
        return StringUtils.substringBetween(
                function.getDefinition(),
                "insert into " + function.getSchema() + ".",
                " (");
    }

    private boolean isFunctionExcluded(String functionName) {
        return excludedFunctions.stream()
                .anyMatch(function -> Objects.equals(function, functionName));
    }

    private List<String> getExcludedFunctions() {
        return PropertiesProvider.getPropertyAsList("exclude.functions");
    }

}
