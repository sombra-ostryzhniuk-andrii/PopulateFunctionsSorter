package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.service.TableService;

import java.util.List;

public class App {

    public static final String schema = "datastaging";

    public static void main(String[] args) {
        TableService tableService = new TableService();

        List<Table> tables = tableService.getAllTablesInSchema(schema);

        tables.forEach(System.out::println);
        System.out.println(tables.size());
    }

    public static String getSchema() {
        return schema;
    }
}
