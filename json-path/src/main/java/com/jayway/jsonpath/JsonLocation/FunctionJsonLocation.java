package com.jayway.jsonpath.JsonLocation;

public class FunctionJsonLocation extends JsonLocation {
    public FunctionJsonLocation(String tagName, AbstractJsonLocation parent) {
        super(tagName, parent);
    }

    @Override
    public String toString() {
        return parent.toString()+"."+tagName;
    }
}
