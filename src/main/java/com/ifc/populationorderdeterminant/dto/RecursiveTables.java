package com.ifc.populationorderdeterminant.dto;

import com.ifc.populationorderdeterminant.entity.Table;

import java.io.Serializable;
import java.util.Objects;

public class RecursiveTables implements Serializable {

    private Table recursiveTable;
    private Table recursiveAt;

    public RecursiveTables(Table recursiveTable, Table recursiveAt) {
        this.recursiveTable = recursiveTable;
        this.recursiveAt = recursiveAt;
    }

    public Table getRecursiveTable() {
        return recursiveTable;
    }

    public Table getRecursiveAt() {
        return recursiveAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecursiveTables that = (RecursiveTables) o;
        return (Objects.equals(recursiveTable, that.recursiveTable) && Objects.equals(recursiveAt, that.recursiveAt))
                || (Objects.equals(recursiveAt, that.recursiveTable) && Objects.equals(recursiveTable, that.recursiveAt));
    }

    @Override
    public int hashCode() {
        return Objects.hash(recursiveTable, recursiveAt) + Objects.hash(recursiveAt, recursiveTable);
    }
}
