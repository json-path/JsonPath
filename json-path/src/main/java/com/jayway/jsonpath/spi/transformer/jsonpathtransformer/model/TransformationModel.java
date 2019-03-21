package com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model;

public class TransformationModel {

    private LookupTable[] lookupTables;

    private PathMapping[] pathMappings;

    public LookupTable[] getLookupTables() {
        return lookupTables;
    }

    public void setLookupTables(LookupTable[] lookupTables) {
        this.lookupTables = lookupTables;
    }

    public PathMapping[] getPathMappings() {
        return pathMappings;
    }

    public void setPathMappings(PathMapping[] pathMappings) {
        this.pathMappings = pathMappings;
    }

    @Override
    public String toString() {
        return "TransformationModel [lookupTables = " + lookupTables + ", pathMappings = " + pathMappings + "]";
    }

}
