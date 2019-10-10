package com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model;

public class PathMapping {

    private String source;

    private SourceTransform additionalTransform;

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
        return " PathMapping [source = " + source + ", target = " + target +
                 ", additionalTransform = " + additionalTransform  + "]";
    }

    public String getLookupTable() {
        return lookupTable;
    }

    public void setLookupTable(String lookupTable) {
        this.lookupTable = lookupTable;
    }

    /**
     * Source is optional if there is a constantSourceValue
     * Treating source as optional allows us to create new values in the target without any reference
     * to the source document.
     */
    public SourceTransform getAdditionalTransform() {
        return additionalTransform;
    }

    public void setAdditionalTransform(SourceTransform additionalTransform) {
        this.additionalTransform = additionalTransform;
    }


}