package com.ifc.myelinflow.service;

import com.ifc.myelinflow.entity.Function;
import com.ifc.myelinflow.providers.ExcludedFunctionsProvider;
import com.ifc.myelinflow.dto.PopulationSequenceResult;
import com.ifc.myelinflow.service.interfaces.ResultPrinterService;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

public class ConsoleResultPrinterService implements ResultPrinterService {

    @Override
    public void print(List<PopulationSequenceResult> results) {
        results.forEach(result -> {

            System.out.println("\nThe population order of the whole " + result.getSchema() + " schema:\n");

            result.getWholeSchemaSequenceSet().forEach(System.out::println);


            result.getSourceSchemasSequenceMap().forEach((sourceSchemas, populationSequenceSet) -> {
                System.out.println("\nThe population order of the " + result.getSchema() + " schema for sources: "
                        + sourceSchemas + "\n");

                populationSequenceSet.forEach(System.out::println);
            });

            printConfigExcludedFunctions(result.getSchema().getName());
            printRuntimeExcludedFunctions(result.getSchema().getName());

            System.out.println("\n\n");
        });
    }

    private void printConfigExcludedFunctions(String schema) {

        Set<Function> excludedFunctions = ExcludedFunctionsProvider.getConfigExcludedFunctionsBySchema(schema);

        if (!CollectionUtils.isEmpty(excludedFunctions)) {
            System.out.println("\n\nExcluded functions by a config file in the schema " + schema + ":\n");
            excludedFunctions.forEach(System.out::println);
        }
    }

    private void printRuntimeExcludedFunctions(String schema) {

        Set<Function> excludedFunctions = ExcludedFunctionsProvider.getRuntimeExcludedFunctionsBySchema(schema);

        if (!CollectionUtils.isEmpty(excludedFunctions)) {
            System.out.println("\nUnable to analyze functions in the schema " + schema + ":\n");
            excludedFunctions.forEach(System.out::println);
        }
    }
}
