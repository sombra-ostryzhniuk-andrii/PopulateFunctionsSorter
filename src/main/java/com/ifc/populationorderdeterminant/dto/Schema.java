package com.ifc.populationorderdeterminant.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class Schema implements Serializable {

    private String name;
    private Integer populationOrder;

    public Schema(String name, String populationOrder) {
        this.name = name;

        try {
            this.populationOrder = Integer.valueOf(populationOrder);
        } catch (NumberFormatException e) {
            throw new RuntimeException("The population order \"" + populationOrder + "\" of the schema " + name +
                    " should be an integer. Please, fix this in the configuration file and try again.", e);
        }
    }

    public Schema(String name, Integer populationOrder) {
        this.name = name;
        this.populationOrder = populationOrder;
    }

    public String getName() {
        return name;
    }

    public Integer getPopulationOrder() {
        return populationOrder;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schema schema = (Schema) o;
        return Objects.equals(name, schema.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
