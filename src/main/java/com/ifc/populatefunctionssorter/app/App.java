package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.entity.Function;
import com.ifc.populatefunctionssorter.service.FunctionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App {

    public static final String SCHEMA = "datastaging";

    public static void main(String[] args) {
        FunctionService functionService = new FunctionService();

        List<Function> functions = functionService.getAllPopulateFunctionsInSchema(SCHEMA);

        for (Function function : functions) {
            System.out.println(function);
        }

        List<Optional> views = new ArrayList<>();
        List<Optional> tables = new ArrayList<>();

        for (Function function : functions) {
            views.add(functionService.getViewNameByFunction(function));
            tables.add(functionService.getTableNameByFunction(function));
        }

        System.out.println("functions: " + functions.size());
        System.out.println("views: " + views.size());
        System.out.println("tables: " + tables.size());
    }

}
