package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.dto.RecursiveTables;
import com.ifc.populationorderdeterminant.entity.Table;
import com.ifc.populationorderdeterminant.service.factories.SequenceFactory;
import com.ifc.populationorderdeterminant.utils.RegexEnum;
import com.ifc.populationorderdeterminant.utils.RegexUtil;
import com.ifc.populationorderdeterminant.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

@Slf4j
public class SequenceService {

    private GraphService graphService = new GraphService();

    public Set<PopulationSequence> getPopulationSequenceSet(DefaultDirectedGraph<Table, DefaultEdge> graph) {
        Map<Table, Set<Table>> parentsMap = graphService.getParentsMap(graph);
        Set<RecursiveTables> recursiveTables = graphService.getRecursiveTables(graph);

        prioritizeRecursiveTables(recursiveTables, parentsMap);

        SequenceFactory sequenceFactory = new SequenceFactory();

        parentsMap.entrySet().removeIf(entry -> {
            Table table = entry.getKey();
            Set<Table> parentsSet = entry.getValue();

            if (parentsSet.isEmpty()) {
                sequenceFactory.addToSequence(table);
                return true;
            }
            return false;
        });

        while (parentsMap.size() > 0) {
            parentsMap.entrySet().removeIf(entry -> {
                Table table = entry.getKey();
                Set<Table> parentsSet = entry.getValue();

                if (sequenceFactory.getTablesInSequence().containsAll(parentsSet)) {
                    sequenceFactory.addToSequence(table);
                    return true;
                }
                return false;
            });
        }

        return sequenceFactory.getSequenceSet();
    }

    private void prioritizeRecursiveTables(Set<RecursiveTables> recursiveTables, Map<Table, Set<Table>> tablesSet) {

        recursiveTables.forEach(recursiveTable -> {

            if (isLeftJoin(recursiveTable.getRecursiveTable(), recursiveTable.getRecursiveAt())) {

                tablesSet.get(recursiveTable.getRecursiveTable()).remove(recursiveTable.getRecursiveAt());

            } else if (isLeftJoin(recursiveTable.getRecursiveAt(), recursiveTable.getRecursiveTable())) {

                tablesSet.get(recursiveTable.getRecursiveAt()).remove(recursiveTable.getRecursiveTable());

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
        final String leftJoinPattern = String.format(RegexEnum.FIND_LEFT_JOIN_PATTERN.value(), table2.toString());
        return RegexUtil.isMatched(viewDefinition, leftJoinPattern);
    }

}
