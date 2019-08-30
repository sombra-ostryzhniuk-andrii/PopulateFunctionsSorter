package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;
import com.ifc.populatefunctionssorter.service.GraphService;
import com.ifc.populatefunctionssorter.service.MatrixService;
import com.ifc.populatefunctionssorter.service.TableService;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import java.util.*;

public class App {

    public static final String schema = "datastaging";

    private static TableService tableService = new TableService();
    private static MatrixService matrixService = new MatrixService();
    private static GraphService graphService = new GraphService();

    public static void main(String[] args) {
        List<Table> tables = tableService.getAllTablesInSchema(schema);

        Map<Table, List<TableReferences>> matrix = matrixService.createReferencesMatrix(tables);

        DefaultDirectedGraph<Table, DefaultEdge> graph = graphService.generateGraph(matrix);

        Iterator<Table> iter = new DepthFirstIterator<>(graph);
        while (iter.hasNext()) {
            Table vertex = iter.next();

            if (vertex.getName().equals("company")) {
                getAllParents(graph, vertex).forEach(System.out::println);
                System.out.println(" ");
                getAllChildren(graph, vertex).forEach(System.out::println);

                graph.incomingEdgesOf(vertex);
                graph.outgoingEdgesOf(vertex);
            }
        }
    }

    protected static Set<Table> getAllParents(DefaultDirectedGraph<Table, DefaultEdge> graph, Table vertex) {
        Set<Table> parents = new HashSet<>();
        EdgeReversedGraph<Table, DefaultEdge> reversedGraph = new EdgeReversedGraph<>(graph);
        BreadthFirstIterator<Table, DefaultEdge> breadthFirstIterator =
                new BreadthFirstIterator<>(reversedGraph, vertex);

        while (breadthFirstIterator.hasNext()) {
            parents.add(breadthFirstIterator.next());
        }
        return parents;
    }

    protected static Set<Table> getAllChildren(DefaultDirectedGraph<Table, DefaultEdge> graph, Table vertex) {
        Set<Table> parents = new HashSet<>();
        BreadthFirstIterator<Table, DefaultEdge> breadthFirstIterator =
                new BreadthFirstIterator<>(graph, vertex);

        while (breadthFirstIterator.hasNext()) {
            parents.add(breadthFirstIterator.next());
        }
        return parents;
    }

    public static String getSchema() {
        return schema;
    }
}
