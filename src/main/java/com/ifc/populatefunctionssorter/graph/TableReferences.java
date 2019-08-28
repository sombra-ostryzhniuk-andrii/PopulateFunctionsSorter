package com.ifc.populatefunctionssorter.graph;

import com.ifc.populatefunctionssorter.entity.Table;

public class TableReferences {

    private Table table;
    private Boolean isReferenced = false;

    public TableReferences(Table table) {
        this.table = table;
    }

    public TableReferences(Table table, Boolean isReferenced) {
        this.table = table;
        this.isReferenced = isReferenced;
    }

    public Table getTable() {
        return table;
    }

    public Boolean getReferenced() {
        return isReferenced;
    }

    public void setReferenced(Boolean referenced) {
        isReferenced = referenced;
    }
}
