package com.jayway.jsonpath.identifier;

import com.jayway.jsonpath.internal.Utils;

import java.util.List;

public class MultiIdentifier extends AbstractIdentifier{
    final List<String> tagNames;
    final AbstractIdentifier parent;

    public MultiIdentifier(List<String> tagNames, AbstractIdentifier parent) {
        this.tagNames = tagNames;
        this.parent = parent;
    }

    @Override
    public String getTagName() {
        return Utils.join(", ", "'", tagNames) ;
    }

    @Override
    public AbstractIdentifier getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return parent.toString() + "["+getTagName()+"]";
    }
}
