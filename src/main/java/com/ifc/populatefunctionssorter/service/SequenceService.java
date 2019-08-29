package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.dto.PopulationSequence;
import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.utils.RegexUtil;

import java.util.*;

public class SequenceService {

    private static final String FIND_TABLE_NAME_PATTERN = "(?i).*?\\b%s\\b.*?";

    private Map<Table, List<Table>> createReferencesMap(List<Table> tables) {
        Map<Table, List<Table>> referencesMap = new HashMap<>();

        tables.parallelStream().forEach(table -> {

            List<Table> dependentTables = new ArrayList<>();

            tables.forEach(innerTable -> {

                final String viewDefinition = innerTable.getView().getDefinition();
                final String pattern = String.format(FIND_TABLE_NAME_PATTERN, table.toString());

                if (!Objects.equals(table, innerTable) && RegexUtil.isMatched(viewDefinition, pattern)) {
                    dependentTables.add(innerTable);
                }
            });

            referencesMap.put(table, dependentTables);
        });

        return referencesMap;
    }

    private Map<Table, List<Table>> getDependenciesMap(List<Table> tables) {
        Map<Table, List<Table>> referencesMap = createReferencesMap(tables);
        Map<Table, List<Table>> dependenciesMap = new HashMap<>();

        tables.parallelStream().forEach(table -> {

            List<Table> dependentOnTables = new ArrayList<>();

            referencesMap.forEach((sourceTable, references) -> references.forEach(targetTable -> {

                if (Objects.equals(table, targetTable) && !Objects.equals(table, sourceTable)) {
                    dependentOnTables.add(sourceTable);
                }
            }));

            dependenciesMap.put(table, dependentOnTables);
        });

        return dependenciesMap;
    }

    public Set<PopulationSequence> getPopulationSequenceSet(List<Table> tables) {
        Map<Table, List<Table>> dependenciesMap = getDependenciesMap(tables);
        SequenceFactory sequenceFactory = new SequenceFactory();

        dependenciesMap.entrySet().removeIf(entry -> {
            Table table = entry.getKey();
            List<Table> dependencies = entry.getValue();

            if (dependencies.isEmpty()) {
                sequenceFactory.addToSequence(table);
                return true;
            }
            return false;
        });

        while (dependenciesMap.size() != 0) {
            dependenciesMap.entrySet().removeIf(entry -> {
                Table table = entry.getKey();
                List<Table> dependencies = entry.getValue();

                if (sequenceFactory.getTablesInSequence().containsAll(dependencies)) {
                    sequenceFactory.addToSequence(table);
                    return true;
                }
                return false;
            });
        }

        return sequenceFactory.getSequenceSet();
    }

}
