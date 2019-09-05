package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.providers.PropertiesProvider;
import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.dto.PopulationSequenceResult;
import com.ifc.populationorderdeterminant.service.interfaces.ResultPrinterService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ConsoleResultPrinterService implements ResultPrinterService {

    @Override
    public void print(List<PopulationSequenceResult> results) {
        results.forEach(result -> {

            System.out.println("\nThe population order of the whole " + result.getSchema() + " schema:\n");

            printPopulationSequence(result.getWholeSchemaSequenceSet());


            result.getSourceSchemasSequenceMap().forEach((sourceSchemas, populationSequenceSet) -> {
                System.out.println("\nThe population order of the " + result.getSchema() + " schema for sources: "
                        + sourceSchemas + "\n");

                printPopulationSequence(populationSequenceSet);
            });


            System.out.println("\n\nExcluded functions in the " + result.getSchema() + " schema:\n");

            PropertiesProvider.getExcludedFunctionsSet(result.getSchema().getName()).forEach(System.out::println);

            System.out.println("\n\n");

        });
    }

    private void printPopulationSequence(Set<PopulationSequence> populationSequenceSet) {
        populationSequenceSet.stream()
                .sorted(Comparator.comparing(PopulationSequence::getSequenceNumber))
                .forEach(System.out::println);
    }
}
