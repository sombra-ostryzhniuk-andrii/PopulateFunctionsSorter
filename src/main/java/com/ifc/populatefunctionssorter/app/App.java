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

    public static final String schema = "datastaging";

    private static TableService tableService = new TableService();
    private static SequenceService sequenceService = new SequenceService();

    public static void main(String[] args) {
        List<Table> tables = tableService.getAllTablesInSchema(schema);

        Set<PopulationSequence> sequenceSet = sequenceService.getPopulationSequenceSet(tables);

        List<PopulationSequence> sequenceList = sequenceSet.stream()
                .sorted(Comparator.comparing(PopulationSequence::getSequenceNumber))
                .collect(Collectors.toList());

        sequenceList.forEach(System.out::println);
    }

    public static String getSchema() {
        return schema;
    }
}
