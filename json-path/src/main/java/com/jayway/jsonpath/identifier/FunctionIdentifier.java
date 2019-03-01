package com.jayway.jsonpath.identifier;

public class FunctionIdentifier extends Identifier {
    public FunctionIdentifier(String tagName, AbstractIdentifier parent) {
        super(tagName, parent);
    }

    @Override
    public String toString() {
        return parent.toString()+"."+tagName;
    }
}
