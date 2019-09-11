package com.ifc.myelinflow.service;

import com.ifc.myelinflow.entity.Function;
import com.ifc.myelinflow.entity.Table;
import com.ifc.myelinflow.entity.View;
import com.ifc.myelinflow.exceptions.UnableAnalyzeFunctionException;
import com.ifc.myelinflow.providers.ExcludedFunctionsProvider;
import com.ifc.myelinflow.repository.TableDAO;
import com.ifc.myelinflow.utils.RegexEnum;
import com.ifc.myelinflow.utils.RegexUtil;
import com.ifc.myelinflow.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableService {

    private FunctionService functionService = new FunctionService();
    private ViewService viewService = new ViewService();

    public Set<Table> getAllTablesInSchema(final String schema) {
        Set<Function> functions = functionService.getAllPopulateFunctionsInSchema(schema);

        return functions.parallelStream()
                .flatMap(function -> {
                    try {
                        final String tableName = getTableNameByFunction(function);

                        final View view = viewService.getViewByFunction(function);

                        return Stream.of(new Table(tableName, view, function, schema));

                    } catch (UnableAnalyzeFunctionException e) {
                        ExcludedFunctionsProvider.addRuntimeExcludedFunction(function, e.getMessage());
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toSet());
    }

    private String getTableNameByFunction(Function function) throws UnableAnalyzeFunctionException {
        String tableName = StringUtils.substringBetween(
                function.getDefinition(),
                "insert into " + function.getSchema() + ".",
                " (");

        if (StringUtils.isEmpty(tableName)) {

            throw new UnableAnalyzeFunctionException("Function " + function + " doesn't match any "
                    + function.getSchema() + " tables.");

        } else if (!TableDAO.isTableExist(tableName, function.getSchema())) {

            throw new UnableAnalyzeFunctionException("Table " + function.getSchema() + "." + tableName +
                    " of the function " + function + " doesn't exist.");
        }
        return StringUtil.validateString(tableName);
    }

    public Set<Table> filterBySourceSchema(Set<Table> tables, final String sourceSchema) {
        Set<Table> filteredTables = new HashSet<>();

        tables.parallelStream().forEach(table -> {

            String viewDefinition = StringUtil.validateSqlScript(table.getView().getDefinition());
            String pattern = String.format(RegexEnum.FIND_SCHEMA_PATTERN.value(), sourceSchema);

            if (RegexUtil.isMatched(viewDefinition, pattern)) {
                filteredTables.add(table);
            }
        });

        return filteredTables;
    }

}
