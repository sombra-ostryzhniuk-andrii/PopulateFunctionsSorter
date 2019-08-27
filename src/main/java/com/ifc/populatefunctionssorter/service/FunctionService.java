package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.app.PropertiesProvider;
import com.ifc.populatefunctionssorter.entity.Function;
import com.ifc.populatefunctionssorter.repository.FunctionDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FunctionService {

    private List<String> excludedFunctions;

    public FunctionService() {
        excludedFunctions = getExcludedFunctions();
    }

    public List<Function> getAllPopulateFunctionsInSchema(String schema) {
        List<Function> functions = FunctionDAO.getAllPopulateFunctionsInSchema(schema);

        if (CollectionUtils.isEmpty(functions)) {
            throw new RuntimeException("Unable to load functions");
        }

        return functions.stream()
                .filter(function -> !isFunctionExcluded(function.getName()))
                .peek(this::validateFunctionDefinition)
                .collect(Collectors.toList());
    }

    public Optional<String> getViewNameByFunction(Function function) {
        String viewName = StringUtils.substringBetween(
                function.getDefinition(),
                "from " + function.getSchema() + ".",
                "\n");

        if (StringUtils.isEmpty(viewName)) {
            log.warn("Function " + function + " doesn't match any views. The result may be invalid");
            return Optional.empty();
        } else {
            return Optional.of(viewName);
        }
    }

    public Optional<String> getTableNameByFunction(Function function) {
        String tableName = StringUtils.substringBetween(
                function.getDefinition(),
                "insert into " + function.getSchema() + ".",
                " (");

        if (StringUtils.isEmpty(tableName)) {
            log.warn("Function " + function + " doesn't match any tables. The result may be invalid");
            return Optional.empty();
        } else {
            return Optional.of(tableName);
        }
    }

    private boolean isFunctionExcluded(String functionName) {
        return excludedFunctions.stream()
                .anyMatch(function -> Objects.equals(function, functionName));
    }

    private List<String> getExcludedFunctions() {
        return PropertiesProvider.getPropertyAsList("exclude.functions");
    }

    private void validateFunctionDefinition(Function function) {
        if (StringUtils.isEmpty(function.getDefinition())) {
            log.warn("Unable to get the definition of function " + function + ". The result may be invalid");
        }
    }

}
