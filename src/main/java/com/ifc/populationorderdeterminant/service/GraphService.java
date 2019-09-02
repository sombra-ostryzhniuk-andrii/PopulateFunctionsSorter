package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.dto.RecursiveTables;
import com.ifc.populationorderdeterminant.entity.Table;
import com.ifc.populationorderdeterminant.utils.RegexUtil;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import java.util.*;
import java.util.stream.Collectors;

public class GraphService {

    private static final String FIND_TABLE_NAME_PATTERN = "(?i).*?\\b%s\\b.*?";

    private Map<Table, Set<Table>> createReferencesMap(Set<Table> tables) {
        Map<Table, Set<Table>> referencesMap = new HashMap<>();

        tables.parallelStream().forEach(table -> {

            Set<Table> dependentTables = new HashSet<>();

            tables.forEach(innerTable -> {

                final String viewDefinition = innerTable.getView().getDefinition();
                final String pattern = String.format(FIND_TABLE_NAME_PATTERN, table.toString());

                if (!table.equals(innerTable) && RegexUtil.isMatched(viewDefinition, pattern)) {
                    dependentTables.add(innerTable);
                }
            });

            referencesMap.put(table, dependentTables);
        });

        return referencesMap;
    }

    public DefaultDirectedGraph<Table, DefaultEdge> generateGraph(Set<Table> tables) {
        Map<Table, Set<Table>> referencesMap = createReferencesMap(tables);

        DefaultDirectedGraph<Table, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        referencesMap.keySet().forEach(graph::addVertex);

        referencesMap.forEach((table, referencesList) -> referencesList.forEach(referencedTable -> {

            if (!table.equals(referencedTable)) {
                graph.addEdge(table, referencedTable);
            }
        }));

        return graph;
    }

    public Map<Table, Set<Table>> getParentsMap(DefaultDirectedGraph<Table, DefaultEdge> graph) {
        Map<Table, Set<Table>> parentsMap = new HashMap<>();

        Iterator<Table> iter = new DepthFirstIterator<>(graph);
        while (iter.hasNext()) {
            Table vertex = iter.next();

            Set<Table> parentsSet = graph.incomingEdgesOf(vertex)
                    .stream()
                    .map(graph::getEdgeSource)
                    .filter(table -> !table.equals(vertex))
                    .collect(Collectors.toSet());

            parentsMap.put(vertex, parentsSet);
        }

        return parentsMap;
    }

    public Map<Table, Set<Table>> getChildrenMap(DefaultDirectedGraph<Table, DefaultEdge> graph) {
        Map<Table, Set<Table>> childrenMap = new HashMap<>();

        Iterator<Table> iter = new DepthFirstIterator<>(graph);
        while (iter.hasNext()) {
            Table vertex = iter.next();

            Set<Table> childrenSet = graph.outgoingEdgesOf(vertex)
                    .stream()
                    .map(graph::getEdgeTarget)
                    .filter(table -> !table.equals(vertex))
                    .collect(Collectors.toSet());

            childrenMap.put(vertex, childrenSet);
        }

        return childrenMap;
    }

    private Set<Table> getAllParents(DefaultDirectedGraph<Table, DefaultEdge> graph, Table vertex) {
        Set<Table> parentsSet = new HashSet<>();
        EdgeReversedGraph<Table, DefaultEdge> reversedGraph = new EdgeReversedGraph<>(graph);
        BreadthFirstIterator<Table, DefaultEdge> breadthFirstIterator = new BreadthFirstIterator<>(reversedGraph, vertex);

        while (breadthFirstIterator.hasNext()) {
            Table parentTable = breadthFirstIterator.next();

            if (!parentTable.equals(vertex)) {
                parentsSet.add(parentTable);
            }
        }
        return parentsSet;
    }

    private Set<Table> getAllChildren(DefaultDirectedGraph<Table, DefaultEdge> graph, Table vertex) {
        Set<Table> childrenSet = new HashSet<>();
        BreadthFirstIterator<Table, DefaultEdge> breadthFirstIterator = new BreadthFirstIterator<>(graph, vertex);

        while (breadthFirstIterator.hasNext()) {
            Table childTable = breadthFirstIterator.next();

            if (!childTable.equals(vertex)) {
                childrenSet.add(childTable);
            }
        }
        return childrenSet;
    }

    private Map<Table, Set<Table>> getAllParentsTreeMap(DefaultDirectedGraph<Table, DefaultEdge> graph) {
        return graph.vertexSet()
                .stream()
                .collect(Collectors.toMap(table -> table, table -> getAllParents(graph, table), (a, b) -> b));
    }

    private Map<Table, Set<Table>> getAllChildrenTreeMap(DefaultDirectedGraph<Table, DefaultEdge> graph) {
        return graph.vertexSet()
                .stream()
                .collect(Collectors.toMap(table -> table, table -> getAllChildren(graph, table), (a, b) -> b));
    }

    public Set<RecursiveTables> getRecursiveTables(DefaultDirectedGraph<Table, DefaultEdge> graph) {
        Map<Table, Set<Table>> allParentsTreeMap = getAllParentsTreeMap(graph);
        Map<Table, Set<Table>> allChildrenTreeMap = getAllChildrenTreeMap(graph);

        Set<RecursiveTables> recursiveTables = new HashSet<>();

        allParentsTreeMap.forEach((table, parents) -> parents.forEach(parentTable -> {

            allChildrenTreeMap.get(table)
                    .stream()
                    .filter(childTable -> childTable.equals(parentTable))
                    .map(childTable -> new RecursiveTables(table, childTable))
                    .forEach(recursiveTables::add);

        }));

        return recursiveTables;
    }

}
