package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.Function;
import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.entity.View;
import com.ifc.populatefunctionssorter.repository.TableDAO;
import java.util.List;
import java.util.stream.Collectors;

public class TableService {

    private FunctionService functionService = new FunctionService();
    private ViewService viewService = new ViewService();

    public List<Table> getAllTablesInSchema(final String schema) {
        List<Function> functions = functionService.getAllPopulateFunctionsInSchema(schema);

        return functions.parallelStream()
                .map(function -> {
                    final String tableName = functionService.getTableNameByFunction(function);
                    validateTableName(tableName, function.getSchema());

                    final String viewName = functionService.getViewNameByFunction(function);
                    final View view = viewService.getViewByName(viewName, function.getSchema());

                    return new Table(tableName, view, function, function.getSchema());
                })
                .collect(Collectors.toList());
    }

    private void validateTableName(String tableName, String schema) {
        if (!TableDAO.isTableExist(tableName, schema)) {
            throw new RuntimeException("Table " + schema + "." + tableName + " doesn't exist");
        }
    }

}
