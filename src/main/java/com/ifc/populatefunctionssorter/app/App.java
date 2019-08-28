package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;
import com.ifc.populatefunctionssorter.service.GraphService;
import com.ifc.populatefunctionssorter.service.MatrixService;
import com.ifc.populatefunctionssorter.service.TableService;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class App {

    public static final String schema = "datastaging";

    private static TableService tableService = new TableService();
    private static MatrixService matrixService = new MatrixService();
    private static GraphService graphService = new GraphService();

    public static void main(String[] args) {
        List<Table> tables = tableService.getAllTablesInSchema(schema);

        Map<Table, List<TableReferences>> matrix = matrixService.createReferencesMatrix(tables);

        SimpleDirectedWeightedGraph<Table, DefaultWeightedEdge> graph = graphService.generateGraph(matrix);

        Iterator<Table> iter = new DepthFirstIterator<>(graph);
        while (iter.hasNext()) {
            Table vertex = iter.next();

            System.out.println(vertex + " : " + graph.inDegreeOf(vertex));

            graph.edgesOf(vertex).forEach(edge -> {
                System.out.println(edge + " : " + graph.getEdgeWeight(edge));
            });
        }
    }

    public static String getSchema() {
        return schema;
    }
}
