package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.providers.ExcludedFunctionsProvider;
import com.ifc.populationorderdeterminant.entity.Function;
import com.ifc.populationorderdeterminant.repository.FunctionDAO;
import com.ifc.populationorderdeterminant.utils.RegexEnum;
import com.ifc.populationorderdeterminant.utils.RegexUtil;
import com.ifc.populationorderdeterminant.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FunctionService {

    private static final String HINT = " add the function to a list of excluded functions in the configuration file.";

    public Set<Function> getAllPopulateFunctionsInSchema(String schema) {
        List<Function> functions = FunctionDAO.getAllPopulateFunctionsInSchema(schema);

        if (CollectionUtils.isEmpty(functions)) {
            throw new RuntimeException("Unable to load functions");
        }

        return functions.stream()
                .filter(function -> !isFunctionExcluded(function))
                .peek(this::validateFunctionDefinition)
                .collect(Collectors.toSet());
    }

    public String getViewNameByFunction(Function function) {
        final String pattern = String.format(RegexEnum.FIND_VIEW_NAME_PATTERN.value(), function.getSchema());

        Optional<String> viewNameOptional = RegexUtil.substring(function.getDefinition(), pattern);

        if (!viewNameOptional.isPresent() || StringUtils.isEmpty(viewNameOptional.get())) {
            throw new RuntimeException("Function " + function + " doesn't match any views," + HINT);
        }
        return StringUtil.validateString(viewNameOptional.get());
    }

    public String getTableNameByFunction(Function function) {
        String tableName = StringUtils.substringBetween(
                function.getDefinition(),
                "insert into " + function.getSchema() + ".",
                " (");

        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("Function " + function + " doesn't match any tables," + HINT);
        }
        return StringUtil.validateString(tableName);
    }

    private boolean isFunctionExcluded(Function function) {
        return ExcludedFunctionsProvider.getAllExcludedFunctions()
                .stream()
                .anyMatch(excludedFunction -> Objects.equals(excludedFunction, function));
    }

    private void validateFunctionDefinition(Function function) {
        if (StringUtils.isEmpty(function.getDefinition())) {
            throw new RuntimeException("Unable to get the definition of the function " + function + ". " +
                    "You may don't have access on this function. Change the function owner or" + HINT);
        }
    }

}
