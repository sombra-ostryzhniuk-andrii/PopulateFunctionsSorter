package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.entity.Function;
import com.ifc.populatefunctionssorter.repository.FunctionsDAO;
import com.ifc.populatefunctionssorter.service.FunctionService;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static final String SCHEMA = "datastaging";

    public static void main(String[] args) {
        FunctionService functionService = new FunctionService();

        List<Function> functions = functionService.getAllPopulateFunctionsInSchema(SCHEMA);

        List<String> views = new ArrayList<>();
        List<String> tables = new ArrayList<>();

        for (Function function : functions) {
            views.add(functionService.getViewNameByFunction(function));
            tables.add(functionService.getTableNameByFunction(function));
        }

        System.out.println("functions: " + functions.size());
        System.out.println("views: " + views.size());
        System.out.println("tables: " + tables.size());
    }

}
