package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;
import com.ifc.populatefunctionssorter.service.MatrixService;
import com.ifc.populatefunctionssorter.service.TableService;

import java.util.List;
import java.util.Map;

public class App {

    public static final String schema = "datastaging";

    private static TableService tableService = new TableService();
    private static MatrixService matrixService = new MatrixService();

    public static void main(String[] args) {
        List<Table> tables = tableService.getAllTablesInSchema(schema);

        Map<Table, List<TableReferences>> matrix = matrixService.createReferencesMatrix(tables);

        System.out.println(matrix.size());

        matrix.forEach((table, tableReferencesList) -> tableReferencesList.forEach(tableReferences -> {
            if (table.getName().equals("company") && tableReferences.isReferenced()) {
                System.out.println(tableReferences.getTable());
            }
        }));
    }

    public static String getSchema() {
        return schema;
    }
}
