package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.entity.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SequenceFactory {

    private final Set<PopulationSequence> sequenceSet = new HashSet<>();
    private int sequenceCounter = 1;

    public void addToSequence(Table table) {
        sequenceSet.add(new PopulationSequence(table, sequenceCounter));
        sequenceCounter++;
    }

    public Set<PopulationSequence> getSequenceSet() {
        return sequenceSet;
    }

    public Set<Table> getTablesInSequence() {
        return sequenceSet.stream()
                .map(PopulationSequence::getTable)
                .collect(Collectors.toSet());
    }

}
