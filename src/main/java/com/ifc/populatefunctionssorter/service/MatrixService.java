package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;
import com.ifc.populatefunctionssorter.utils.RegexUtil;

import java.util.*;

public class MatrixService {

    private static final String FIND_TABLE_NAME_PATTERN = "(?i).*?\\b%s\\b.*?";

    public Map<Table, List<TableReferences>> createMatrix(List<Table> tables) {
        Map<Table, List<TableReferences>> matrix = new HashMap<>();

        tables.forEach(table -> {

            List<TableReferences> tableReferences = new LinkedList<>();

            tables.forEach(innerTable -> tableReferences.add(new TableReferences(innerTable)));

            matrix.put(table, tableReferences);
        });

        return matrix;
    }

    public Map<Table, List<TableReferences>> createReferencesMatrix(List<Table> tables) {
        Map<Table, List<TableReferences>> matrix = createMatrix(tables);

        matrix.forEach((table, tableReferencesList) -> {
            tableReferencesList.parallelStream().forEach(tableReferences -> {

                if (!table.equals(tableReferences.getTable())) {

                    final String viewDefinition = tableReferences.getTable().getView().getDefinition();
                    final String pattern = String.format(FIND_TABLE_NAME_PATTERN, table.toString());

                    tableReferences.setReferenced(RegexUtil.isMatched(viewDefinition, pattern));
                }
            });
        });

        return matrix;
    }

}
