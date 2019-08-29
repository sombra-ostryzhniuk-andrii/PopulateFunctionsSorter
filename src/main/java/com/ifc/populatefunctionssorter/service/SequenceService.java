package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.dto.PopulationSequence;
import com.ifc.populatefunctionssorter.dto.RecursiveTables;
import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.utils.RegexUtil;
import com.ifc.populatefunctionssorter.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
public class SequenceService {

    private static final String FIND_TABLE_NAME_PATTERN = "(?i).*?\\b%s\\b.*?";
    private static final String FIND_LEFT_JOIN_PATTERN = "(?i).*?left join %s\\b.*?";


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

    private Map<Table, List<Table>> getDependenciesMap(Map<Table, List<Table>> referencesMap) {
        Map<Table, List<Table>> dependenciesMap = new HashMap<>();

        referencesMap.keySet().parallelStream().forEach(table -> {

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
        Map<Table, List<Table>> referencesMap = createReferencesMap(tables);
        Map<Table, List<Table>> dependenciesMap = getDependenciesMap(referencesMap);

        SequenceFactory sequenceFactory = new SequenceFactory();

        Set<RecursiveTables> recursiveTables = getRecursiveTables(dependenciesMap, referencesMap);
        prioritizeRecursiveTables(recursiveTables, dependenciesMap);

        dependenciesMap.entrySet().removeIf(entry -> {
            Table table = entry.getKey();
            List<Table> dependencies = entry.getValue();

            if (dependencies.isEmpty()) {
                sequenceFactory.addToSequence(table);
                return true;
            }
            return false;
        });

        while (dependenciesMap.size() > 0) {
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

    private Set<RecursiveTables> getRecursiveTables(Map<Table, List<Table>> dependenciesMap,
                                                    Map<Table, List<Table>> referencesMap) {


        Set<RecursiveTables> recursiveTables = new HashSet<>();

        dependenciesMap.forEach((table, dependencies) -> dependencies.forEach(dependentTable -> {

            recursiveTables.addAll(getTableTree(referencesMap, table, dependentTable));
        }));

/*        dependenciesMap.forEach((table, dependencies) -> dependencies.forEach(dependentTable -> {

            referencesMap.get(table).forEach(referencedTable -> {

                if (dependentTable.equals(referencedTable)) {
                    recursiveTables.add(new RecursiveTables(table, dependentTable));
                }
            });
        }));*/

        return recursiveTables;
    }

    private Set<RecursiveTables> getTableTree(Map<Table, List<Table>> referencesMap, Table table, Table dependentTable) {
        Set<RecursiveTables> recursiveTables = new HashSet<>();

        referencesMap.get(table).forEach(referencedTable -> {

            if (dependentTable.equals(referencedTable)) {
                recursiveTables.add(new RecursiveTables(table, dependentTable));
            }

            recursiveTables.addAll(getTableTree(referencesMap, referencedTable, table));
        });

        return recursiveTables;
    }

/*    private Set<RecursiveTables> getRecursiveTables(Map<Table, List<Table>> dependenciesMap) {
        Set<RecursiveTables> recursiveTables = new HashSet<>();

        dependenciesMap.forEach((table, dependencies) -> dependencies.forEach(dependentTable -> {

            getRecursiveTable(table, dependentTable, dependenciesMap).ifPresent(recursiveTables::add);

        }));
        return recursiveTables;
    }

    private Optional<RecursiveTables> getRecursiveTable(Table tableToCheckRecursion,
                                                        Table subDependentTable,
                                                        Map<Table, List<Table>> dependenciesMap) {

        for (Table table : dependenciesMap.get(subDependentTable)) {

            return table.equals(tableToCheckRecursion)
                    ? Optional.of(new RecursiveTables(tableToCheckRecursion, subDependentTable))
                    : getRecursiveTable(subDependentTable, table, dependenciesMap);
        }
        return Optional.empty();
    }*/

    private void prioritizeRecursiveTables(Set<RecursiveTables> recursiveTables, Map<Table, List<Table>> dependenciesMap) {

        recursiveTables.forEach(recursiveTable -> {

            if (isLeftJoin(recursiveTable.getRecursiveTable(), recursiveTable.getRecursiveAt())) {

                dependenciesMap.get(recursiveTable.getRecursiveTable()).remove(recursiveTable.getRecursiveAt());

            } else if (isLeftJoin(recursiveTable.getRecursiveAt(), recursiveTable.getRecursiveTable())) {

                dependenciesMap.get(recursiveTable.getRecursiveAt()).remove(recursiveTable.getRecursiveTable());

            } else {
                throw new RuntimeException("Tables " + recursiveTable.getRecursiveTable() + " and " + recursiveTable.getRecursiveAt() +
                        " cause a recursion and both are joined not by a LEFT JOIN. Population is not possible." +
                        " Please, add one of their functions to a list of excluded functions in the configuration file " +
                        " or fix the DB structure and try again.");
            }
        });
    }

    private Boolean isLeftJoin(Table table1, Table table2) {
        final String viewDefinition = StringUtil.validateSqlScript(table1.getView().getDefinition());
        final String leftJoinPattern = String.format(FIND_LEFT_JOIN_PATTERN, table2.toString());
        return RegexUtil.isMatched(viewDefinition, leftJoinPattern);
    }

}
