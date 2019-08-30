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

    private static final String schema = "dw";
    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;

        TableService tableService = new TableService();
        SequenceService sequenceService = new SequenceService();

        System.out.println("Process is running...");

        List<Table> tables = tableService.getAllTablesInSchema(schema);

        Set<PopulationSequence> sequenceSet = sequenceService.getPopulationSequenceSet(tables);

        List<PopulationSequence> sequenceList = sequenceSet.stream()
                .sorted(Comparator.comparing(PopulationSequence::getSequenceNumber))
                .collect(Collectors.toList());

        System.out.println("\n\nThe population order:\n");
        sequenceList.forEach(System.out::println);

        System.out.println("\n\nExcluded functions:\n");
        PropertiesProvider.getExcludedFunctions().forEach(System.out::println);
    }

    public static String getSchema() {
        return schema;
    }

    static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
