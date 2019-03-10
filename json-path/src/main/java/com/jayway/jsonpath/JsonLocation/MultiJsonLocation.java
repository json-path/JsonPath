package com.jayway.jsonpath.JsonLocation;

import com.jayway.jsonpath.internal.Utils;

import java.util.List;

public class MultiJsonLocation extends AbstractJsonLocation {
    final List<String> tagNames;
    final AbstractJsonLocation parent;

    public MultiJsonLocation(List<String> tagNames, AbstractJsonLocation parent) {
        this.tagNames = tagNames;
        this.parent = parent;
    }

    @Override
    public String getTagName() {
        return Utils.join(", ", "'", tagNames) ;
    }

    @Override
    public AbstractJsonLocation getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return parent.toString() + "["+getTagName()+"]";
    }
}
