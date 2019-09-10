package com.ifc.populationorderdeterminant.dto;

import com.ifc.populationorderdeterminant.entity.Function;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PopulationSequenceResult implements Serializable {

    private Schema schema;
    private TreeSet<PopulationSequence> wholeSchemaSequenceSet;
    private Map<SourceSchemas, TreeSet<PopulationSequence>> sourceSchemasSequenceMap;
    private Set<Function> configExcludedFunctions;
    private Set<Function> runtimeExcludedFunctions;

    public PopulationSequenceResult(Schema schema) {
        this.schema = schema;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public TreeSet<PopulationSequence> getWholeSchemaSequenceSet() {
        return wholeSchemaSequenceSet;
    }

    public void setWholeSchemaSequenceSet(TreeSet<PopulationSequence> wholeSchemaSequenceSet) {
        this.wholeSchemaSequenceSet = wholeSchemaSequenceSet;
    }

    public Map<SourceSchemas, TreeSet<PopulationSequence>> getSourceSchemasSequenceMap() {
        return sourceSchemasSequenceMap;
    }

    public void setSourceSchemasSequenceMap(Map<SourceSchemas, TreeSet<PopulationSequence>> sourceSchemasSequenceMap) {
        this.sourceSchemasSequenceMap = sourceSchemasSequenceMap;
    }

    public Set<Function> getConfigExcludedFunctions() {
        return configExcludedFunctions;
    }

    public void setConfigExcludedFunctions(Set<Function> configExcludedFunctions) {
        this.configExcludedFunctions = configExcludedFunctions;
    }

    public Set<Function> getRuntimeExcludedFunctions() {
        return runtimeExcludedFunctions;
    }

    public void setRuntimeExcludedFunctions(Set<Function> runtimeExcludedFunctions) {
        this.runtimeExcludedFunctions = runtimeExcludedFunctions;
    }
}
