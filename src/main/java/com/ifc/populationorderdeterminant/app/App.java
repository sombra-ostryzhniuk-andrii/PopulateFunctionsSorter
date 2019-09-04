package com.ifc.populationorderdeterminant.app;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.dto.Result;
import com.ifc.populationorderdeterminant.dto.SourceSchemas;
import com.ifc.populationorderdeterminant.entity.Table;
import com.ifc.populationorderdeterminant.service.ConsoleResultPrinterService;
import com.ifc.populationorderdeterminant.service.GraphService;
import com.ifc.populationorderdeterminant.service.SequenceService;
import com.ifc.populationorderdeterminant.service.TableService;
import com.ifc.populationorderdeterminant.service.interfaces.ResultPrinterService;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    private static String[] arguments;
    private static final String DATASTAGING_SCHEMA = "datastaging";
    private static final String DW_SCHEMA = "dw";

    public static void main(String[] args) {
        arguments = args;

        TableService tableService = new TableService();
        GraphService graphService = new GraphService();
        SequenceService sequenceService = new SequenceService();
        ResultPrinterService printerService = new ConsoleResultPrinterService();

        System.out.println("Process is running...");

        List<Result> results = new ArrayList<>();

        PropertiesProvider.getSchemas().forEach(schema -> {
            System.out.println("Processing of the " + schema + " schema...");

            Result result = new Result(schema);

            Set<Table> tables = tableService.getAllTablesInSchema(schema);
            DefaultDirectedGraph<Table, DefaultEdge> graph = graphService.generateGraph(tables);

            result.setWholeSchemaSequenceSet(sequenceService.getPopulationSequenceSet(graph));


            Map<SourceSchemas, Set<PopulationSequence>> sourceSchemasSequenceMap = new HashMap<>();

            PropertiesProvider.getSourceSchemasSet().forEach(sourceSchemas -> {

                DefaultDirectedGraph<Table, DefaultEdge> sourceSchemasGraph;

                if (schema.equals(DATASTAGING_SCHEMA)) {

                    sourceSchemasGraph = graphService.getChildrenGraphForSourceSchemas(graph, sourceSchemas);

                } else if (schema.equals(DW_SCHEMA)) {

                    Set<Table> sourceTables = results.stream()
                            .filter(existingResult -> existingResult.getSchema().equals(DATASTAGING_SCHEMA))
                            .map(existingResult -> existingResult.getSourceSchemasSequenceMap().get(sourceSchemas))
                            .flatMap(Set::stream)
                            .map(PopulationSequence::getTable)
                            .collect(Collectors.toSet());

                    sourceSchemasGraph = graphService.getChildrenGraphForSourceTables(graph, sourceTables);

                } else {
                    throw new RuntimeException("Datastaging schema has not been analyzed yet");
                }

                sourceSchemasSequenceMap.put(sourceSchemas, sequenceService.getPopulationSequenceSet(sourceSchemasGraph));
            });

            result.setSourceSchemasSequenceMap(sourceSchemasSequenceMap);


            result.setConfigExcludedFunctions(PropertiesProvider.getExcludedFunctionsSet(schema));

            results.add(result);
        });

        printerService.print(results);
    }

    static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
