package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.Function;
import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.entity.View;
import java.util.List;
import java.util.stream.Collectors;

public class TableService {

    private FunctionService functionService = new FunctionService();
    private ViewService viewService = new ViewService();

    public List<Table> getAllTablesInSchema(final String schema) {
        List<Function> functions = functionService.getAllPopulateFunctionsInSchema(schema);

        return functions.stream()
                .map(function -> {
                    final String tableName = functionService.getTableNameByFunction(function);

                    final String viewName = functionService.getViewNameByFunction(function);
                    final View view = viewService.getViewByName(viewName, function.getSchema());

                    return new Table(tableName, view, function, schema);
                })
                .collect(Collectors.toList());
    }

}
