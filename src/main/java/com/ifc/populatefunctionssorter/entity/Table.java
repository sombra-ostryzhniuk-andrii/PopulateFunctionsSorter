package com.ifc.populatefunctionssorter.entity;

public class Table {

    private String name;
    private View view;
    private Function function;
    private String schema;

    public Table() {
    }

    public Table(String name, View view, Function function) {
        this.name = name;
        this.view = view;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
