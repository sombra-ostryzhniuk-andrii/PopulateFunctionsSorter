package com.ifc.myelinflow.service;

import com.ifc.myelinflow.dto.RecursiveTables;
import com.ifc.myelinflow.dto.SourceSchemas;
import com.ifc.myelinflow.entity.Table;
import com.ifc.myelinflow.utils.RegexEnum;
import com.ifc.myelinflow.utils.RegexUtil;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import java.util.*;
import java.util.stream.Collectors;

public class GraphService {

    private TableService tableService = new TableService();

    private Map<Table, Set<Table>> createReferencesMap(Set<Table> tables) {
        return tables.parallelStream()
                .collect(Collectors.toMap(table -> table, table -> getReferencesSet(table, tables), (a, b) -> b));
    }

    private Set<Table> getReferencesSet(Table tableToCheck, Set<Table> tables) {
        Set<Table> dependentTables = new HashSet<>();
        
        tables.forEach(table -> {

            final String viewDefinition = table.getView().getDefinition();
            final String pattern = String.format(RegexEnum.FIND_TABLE_NAME_PATTERN.value(), tableToCheck.toString());

            if (!tableToCheck.equals(table) && RegexUtil.isMatched(viewDefinition, pattern)) {
                dependentTables.add(table);
            }
        });
        
        return dependentTables;
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

    public DefaultDirectedGraph<Table, DefaultEdge> getChildrenGraphForSourceSchemas (
            DefaultDirectedGraph<Table, DefaultEdge> wholeSchemaGraph,
            SourceSchemas sourceSchemas) {

        Set<Table> tables = wholeSchemaGraph.vertexSet();

        Set<Table> primaryTables = sourceSchemas.getSchemas()
                .stream()
                .map(sourceSchema -> tableService.filterBySourceSchema(tables, sourceSchema))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        return getChildrenGraph(wholeSchemaGraph, primaryTables);
    }

    public DefaultDirectedGraph<Table, DefaultEdge> getChildrenGraphForSourceTables (
            DefaultDirectedGraph<Table, DefaultEdge> wholeSchemaGraph,
            Set<Table> sourceTables) {

        Set<Table> tables = wholeSchemaGraph.vertexSet();

        Set<Table> primaryTables = sourceTables
                .stream()
                .map(table -> getReferencesSet(table, tables))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        return getChildrenGraph(wholeSchemaGraph, primaryTables);
    }

    public DefaultDirectedGraph<Table, DefaultEdge> getChildrenGraph(
            DefaultDirectedGraph<Table, DefaultEdge> wholeSchemaGraph,
            Set<Table> primaryTables) {

        Set<Table> allTables = wholeSchemaGraph.vertexSet();

        Set<Table> childrenTables = new HashSet<>(primaryTables);
        primaryTables.forEach(table -> childrenTables.addAll(getAllChildren(wholeSchemaGraph, table)));

        DefaultDirectedGraph<Table, DefaultEdge> childrenGraph = (DefaultDirectedGraph<Table, DefaultEdge>) wholeSchemaGraph.clone();

        allTables.stream()
                .filter(table -> !childrenTables.contains(table))
                .forEach(childrenGraph::removeVertex);

        return childrenGraph;
    }

}
