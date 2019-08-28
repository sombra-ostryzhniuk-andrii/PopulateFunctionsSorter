package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import java.util.List;
import java.util.Map;

public class GraphService {

    public SimpleDirectedWeightedGraph<Table, DefaultWeightedEdge> generateGraph(Map<Table, List<TableReferences>> matrix) {

        SimpleDirectedWeightedGraph<Table, DefaultWeightedEdge> graph =
                new SimpleDirectedWeightedGraph<Table, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        matrix.keySet().forEach(graph::addVertex);

        matrix.forEach((table, tableReferencesList) -> tableReferencesList.forEach(tableReferences -> {

            if (tableReferences.isReferenced()) {
                graph.addEdge(table, tableReferences.getTable());
            }
        }));

        return graph;
    }

}
