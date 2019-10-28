package com.ifc.myelinflow.service;

import com.ifc.myelinflow.providers.ExcludedFunctionsProvider;
import com.ifc.myelinflow.providers.PropertiesProvider;
import com.ifc.myelinflow.dto.*;
import com.ifc.myelinflow.entity.Table;
import com.ifc.myelinflow.service.factories.SequenceFactory;
import com.ifc.myelinflow.utils.RegexEnum;
import com.ifc.myelinflow.utils.RegexUtil;
import com.ifc.myelinflow.utils.StringUtil;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.util.*;
import java.util.stream.Collectors;

public class SequenceService {

    private GraphService graphService = new GraphService();
    private TableService tableService = new TableService();

    private static final String FIRST_SCHEMA = "datastaging";
    private List<String> nassTables = Arrays.asList("nassproductionpractice","nassaggregationlevel","nassclass","nassgroup",
            "nassfrequency","nassutilitypractice","nassstatisticcategory","nasswatershed","nasssector","nasscommodity",
            "nassunitofmeasure","nassdomain","nassregion","nassstate","nassdistrict","nasssource","nasscountry","nasscounty");

    public PopulationSequenceResult getPopulationSequenceResult(Schema schema, List<PopulationSequenceResult> results) {
        PopulationSequenceResult result = new PopulationSequenceResult(schema);

        Set<Table> tables = tableService.getAllTablesInSchema(schema.getName());
        DefaultDirectedGraph<Table, DefaultEdge> graph = graphService.generateGraph(tables);

        result.setWholeSchemaSequenceSet(getPopulationSequenceSet(graph));

        Map<SourceSchemas, TreeSet<PopulationSequence>> sourceSchemasSequenceMap = new HashMap<>();

        PropertiesProvider.getSourceSchemasSet().forEach(sourceSchemas -> {

            DefaultDirectedGraph<Table, DefaultEdge> sourceSchemasGraph;

            if (results.isEmpty()) {
//                sourceSchemasGraph = graphService.getChildrenGraphForSourceSchemas(graph, sourceSchemas);

                Set<Table> sourceTables = nassTables.stream()
                        .map(tableName -> new Table(tableName, FIRST_SCHEMA))
                        .collect(Collectors.toSet());

                sourceSchemasGraph = graphService.getChildrenGraphForSourceTables(graph, sourceTables);
            } else {
                Set<Table> sourceTables = results.stream()
                        .filter(prevResult -> prevResult.getSchema().getPopulationOrder() == schema.getPopulationOrder() - 1)
                        .map(existingResult -> existingResult.getSourceSchemasSequenceMap().get(sourceSchemas))
                        .flatMap(Set::stream)
                        .map(PopulationSequence::getTable)
                        .collect(Collectors.toSet());

                sourceSchemasGraph = graphService.getChildrenGraphForSourceTables(graph, sourceTables);
            }

            sourceSchemasSequenceMap.put(sourceSchemas, getPopulationSequenceSet(sourceSchemasGraph));
        });

        result.setSourceSchemasSequenceMap(sourceSchemasSequenceMap);

        return result;
    }

    private TreeSet<PopulationSequence> getPopulationSequenceSet(DefaultDirectedGraph<Table, DefaultEdge> graph) {
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

        int previousTablesSize = parentsMap.size();
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

            if (previousTablesSize == parentsMap.size()) {
                throw new RuntimeException("Unable to analyze the population order. " +
                        "The population chain is missing some table or recursion has occurred. " +
                        "Please, check the excluded functions and the database structure and try again.");
            }
            previousTablesSize = parentsMap.size();
        }

        return sequenceFactory.getSequenceSet();
    }

    private void prioritizeRecursiveTables(Set<RecursiveTables> recursiveTables, Map<Table, Set<Table>> tablesMap) {

        recursiveTables.forEach(recursiveTable -> {

            if (isLeftJoin(recursiveTable.getRecursiveTable(), recursiveTable.getRecursiveAt())) {

                tablesMap.get(recursiveTable.getRecursiveTable()).remove(recursiveTable.getRecursiveAt());

            } else if (isLeftJoin(recursiveTable.getRecursiveAt(), recursiveTable.getRecursiveTable())) {

                tablesMap.get(recursiveTable.getRecursiveAt()).remove(recursiveTable.getRecursiveTable());

            } else {
                String message = "Tables " + recursiveTable.getRecursiveTable() + " and " + recursiveTable.getRecursiveAt() +
                        " cause a recursion and both are joined not by a LEFT JOIN. Population is not possible.";

                ExcludedFunctionsProvider.addRuntimeExcludedFunction(
                        recursiveTable.getRecursiveTable().getFunction(),
                        message);

                tablesMap.remove(recursiveTable.getRecursiveTable());
            }
        });
    }

    private Boolean isLeftJoin(Table table1, Table table2) {
        final String viewDefinition = StringUtil.validateSqlScript(table1.getView().getDefinition());
        final String leftJoinPattern = String.format(RegexEnum.FIND_LEFT_JOIN_PATTERN.value(), table2.toString());
        return RegexUtil.isMatched(viewDefinition, leftJoinPattern);
    }

}
