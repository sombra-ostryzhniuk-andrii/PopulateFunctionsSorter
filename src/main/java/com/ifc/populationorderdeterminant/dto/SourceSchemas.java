package com.ifc.populationorderdeterminant.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class SourceSchemas implements Serializable {

    private String key;
    private Set<String> schemas;

    public SourceSchemas(String key, Set<String> schemas) {
        this.key = key;
        this.schemas = schemas;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Set<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(Set<String> schemas) {
        this.schemas = schemas;
    }

    @Override
    public String toString() {
        return schemas.toString().replaceAll("(\\[|\\])", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceSchemas that = (SourceSchemas) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
