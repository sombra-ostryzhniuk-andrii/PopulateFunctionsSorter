package com.ifc.populationorderdeterminant.dto;

import com.ifc.populationorderdeterminant.entity.Table;

import java.io.Serializable;
import java.util.Objects;

public class PopulationSequence implements Serializable {

    private Table table;
    private Integer sequenceNumber;

    public PopulationSequence(Table table, Integer sequenceNumber) {
        this.table = table;
        this.sequenceNumber = sequenceNumber;
    }

    public Table getTable() {
        return table;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String toString() {
        return table + " : " + sequenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopulationSequence that = (PopulationSequence) o;
        return Objects.equals(table, that.table);
    }

    @Override
    public int hashCode() {
        return table.hashCode();
    }
}
