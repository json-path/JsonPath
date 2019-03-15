package com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model;

public class PathMapping {
    private String source;

    private String target;

    private String lookupTable;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "ClassPojo [source = " + source + ", target = " + target +  "]";
    }

    public String getLookupTable() {
        return lookupTable;
    }

    public void setLookupTable(String lookupTable) {
        this.lookupTable = lookupTable;
    }
}