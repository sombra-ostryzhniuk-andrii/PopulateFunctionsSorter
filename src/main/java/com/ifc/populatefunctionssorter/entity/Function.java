package com.ifc.populatefunctionssorter.entity;

public class Function {

    private String name;
    private String definition;
    private String schema;

    public Function() {
    }

    public Function(String name, String definition) {
        this.name = name.toLowerCase();
        this.definition = definition.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition.toLowerCase();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
