package com.ifc.populationorderdeterminant.service.factories;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.entity.Table;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SequenceFactory {

    private final TreeSet<PopulationSequence> sequenceSet = new TreeSet<>();
    private int sequenceCounter = 1;

    public void addToSequence(Table table) {
        sequenceSet.add(new PopulationSequence(table, sequenceCounter));
        sequenceCounter++;
    }

    public TreeSet<PopulationSequence> getSequenceSet() {
        return sequenceSet;
    }

    public Set<Table> getTablesInSequence() {
        return sequenceSet.stream()
                .map(PopulationSequence::getTable)
                .collect(Collectors.toSet());
    }

}
