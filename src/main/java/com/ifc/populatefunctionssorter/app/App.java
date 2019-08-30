package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.dto.PopulationSequence;
import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.service.SequenceService;
import com.ifc.populatefunctionssorter.service.TableService;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class App {

    private static final String schema = "datastaging";
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
