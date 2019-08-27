package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.app.PropertiesProvider;
import com.ifc.populatefunctionssorter.entity.Function;
import com.ifc.populatefunctionssorter.repository.FunctionDAO;
import com.ifc.populatefunctionssorter.utils.RegexUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FunctionService {

    private List<String> excludedFunctions;
    private static final String HINT = " Add the function to a list of excluded functions in the configuration file.";
    private static final String VIEW_NAME_PATTERN = "(?i)from %s.(.+?)( |\n|\t|;)";

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

    public String getViewNameByFunction(Function function) {
        final String pattern = String.format(VIEW_NAME_PATTERN, function.getSchema());

        Optional<String> viewNameOptional = RegexUtil.substring(function.getDefinition(), pattern);

        if (!viewNameOptional.isPresent() || StringUtils.isEmpty(viewNameOptional.get())) {
            throw new RuntimeException("Function " + function + " doesn't match any views." + HINT);
        }
        return viewNameOptional.get().toLowerCase().trim();
    }

    public String getTableNameByFunction(Function function) {
        String tableName = StringUtils.substringBetween(
                function.getDefinition(),
                "insert into " + function.getSchema() + ".",
                " (");

        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("Function " + function + " doesn't match any tables." + HINT);
        }
        return tableName.toLowerCase().trim();
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
            throw new RuntimeException("Unable to get the definition of the function " + function + "." + HINT);
        }
    }

}
