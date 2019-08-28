package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.Table;
import com.ifc.populatefunctionssorter.graph.TableReferences;

import java.util.*;

public class MatrixService {

    public static Map<Table, List<TableReferences>> createMatrix(List<Table> tables) {
        Map<Table, List<TableReferences>> matrix = new HashMap<>();

        for (int i = 0; i < tables.size(); i++) {

            List<TableReferences> tableReferences = new LinkedList<>();

            for (int j = i+1; j < tables.size(); j++) {
                tableReferences.add(new TableReferences(tables.get(j)));
            }

            if (!tableReferences.isEmpty()) {
                matrix.put(tables.get(i), tableReferences);
            }
        }

        return matrix;
    }

}
