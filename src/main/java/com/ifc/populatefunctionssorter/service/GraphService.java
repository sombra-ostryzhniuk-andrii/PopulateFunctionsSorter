package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;
import java.util.List;
import java.util.Map;

public class GraphService {

    public DefaultDirectedGraph<Table, DefaultEdge> generateGraph(Map<Table, List<TableReferences>> matrix) {

        DefaultDirectedGraph<Table, DefaultEdge> graph =
                new DefaultDirectedGraph<Table, DefaultEdge>(DefaultEdge.class);

        matrix.keySet().forEach(graph::addVertex);

        matrix.forEach((table, tableReferencesList) -> tableReferencesList.forEach(tableReferences -> {

            if (tableReferences.isReferenced()) {
                graph.addEdge(table, tableReferences.getTable());
            }
        }));

        return graph;
    }

}
