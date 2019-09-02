package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.entity.Function;
import com.ifc.populationorderdeterminant.entity.Table;
import com.ifc.populationorderdeterminant.entity.View;
import com.ifc.populationorderdeterminant.repository.TableDAO;
import java.util.Set;
import java.util.stream.Collectors;

public class TableService {

    private FunctionService functionService = new FunctionService();
    private ViewService viewService = new ViewService();

    public Set<Table> getAllTablesInSchema(final String schema) {
        Set<Function> functions = functionService.getAllPopulateFunctionsInSchema(schema);

        return functions.parallelStream()
                .map(function -> {
                    final String tableName = functionService.getTableNameByFunction(function);
                    validateTableName(tableName, function.getSchema());

                    final String viewName = functionService.getViewNameByFunction(function);
                    final View view = viewService.getViewByName(viewName, function.getSchema());

                    return new Table(tableName, view, function, function.getSchema());
                })
                .collect(Collectors.toSet());
    }

    private void validateTableName(String tableName, String schema) {
        if (!TableDAO.isTableExist(tableName, schema)) {
            throw new RuntimeException("Table " + schema + "." + tableName + " doesn't exist");
        }
    }

}
