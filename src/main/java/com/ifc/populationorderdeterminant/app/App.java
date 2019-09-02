package com.ifc.populationorderdeterminant.app;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.entity.Table;
import com.ifc.populationorderdeterminant.service.SequenceService;
import com.ifc.populationorderdeterminant.service.TableService;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class App {

    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;

        TableService tableService = new TableService();
        SequenceService sequenceService = new SequenceService();

        System.out.println("Process is running...");

        PropertiesProvider.getSchemas().forEach(schema -> {
            System.out.println("Processing of the " + schema + " schema...");

            List<Table> tables = tableService.getAllTablesInSchema(schema);
            Set<PopulationSequence> sequenceSet = sequenceService.getPopulationSequenceSet(tables);

            List<PopulationSequence> sequenceList = sequenceSet.stream()
                    .sorted(Comparator.comparing(PopulationSequence::getSequenceNumber))
                    .collect(Collectors.toList());

            System.out.println("\nThe population order of the " + schema + " schema:\n");
            sequenceList.forEach(System.out::println);

            System.out.println("\n\nExcluded functions in the " + schema + " schema:\n");
            PropertiesProvider.getExcludedFunctions(schema).forEach(System.out::println);
            System.out.println("\n\n");
        });
    }

    static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
