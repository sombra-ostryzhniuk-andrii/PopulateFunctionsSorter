package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;
import com.ifc.populatefunctionssorter.service.MatrixService;
import com.ifc.populatefunctionssorter.service.TableService;

import java.util.List;
import java.util.Map;

public class App {

    public static final String schema = "datastaging";

    public static void main(String[] args) {
        TableService tableService = new TableService();
        List<Table> tables = tableService.getAllTablesInSchema(schema);

        Map<Table, List<TableReferences>> matrix = MatrixService.createMatrix(tables);

        System.out.println(matrix.size());
    }

    public static String getSchema() {
        return schema;
    }
}
