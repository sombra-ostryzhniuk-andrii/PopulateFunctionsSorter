package com.ifc.populationorderdeterminant.app;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.dto.SourceSchemas;
import com.ifc.populationorderdeterminant.entity.Table;
import com.ifc.populationorderdeterminant.service.GraphService;
import com.ifc.populationorderdeterminant.service.SequenceService;
import com.ifc.populationorderdeterminant.service.TableService;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.stream.Collectors;

public class App {

    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;

        TableService tableService = new TableService();
        GraphService graphService = new GraphService();

        System.out.println("Process is running...");

        PropertiesProvider.getSchemas().forEach(schema -> {
            System.out.println("Processing of the " + schema + " schema...");

            Set<Table> tables = tableService.getAllTablesInSchema(schema);
            DefaultDirectedGraph<Table, DefaultEdge> graph = graphService.generateGraph(tables);

            System.out.println("\nThe population order of the " + schema + " schema:\n");
            printSequence(graph);

            PropertiesProvider.getSourceSchemasSet().forEach(sourceSchemas -> {
                DefaultDirectedGraph<Table, DefaultEdge> sourceSchemasGraph =
                        graphService.getChildrenGraphForSourceSchemas(graph, sourceSchemas);

                System.out.println("\nThe population order of the " + schema + " schema for sources: " + sourceSchemas + "\n");
                printSequence(sourceSchemasGraph);
            });

            System.out.println("\n\nExcluded functions in the " + schema + " schema:\n");
            PropertiesProvider.getExcludedFunctionsSet(schema).forEach(System.out::println);
            System.out.println("\n\n");
        });
    }

    private static void printSequence(DefaultDirectedGraph<Table, DefaultEdge> graph) {
        SequenceService sequenceService = new SequenceService();

        Set<PopulationSequence> sequenceSet = sequenceService.getPopulationSequenceSet(graph);

        List<PopulationSequence> sequenceList = sequenceSet.stream()
                .sorted(Comparator.comparing(PopulationSequence::getSequenceNumber))
                .collect(Collectors.toList());

        sequenceList.forEach(System.out::println);
    }

    static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
