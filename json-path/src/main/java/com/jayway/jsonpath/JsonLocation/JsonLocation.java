package com.jayway.jsonpath.JsonLocation;

public  class JsonLocation extends AbstractJsonLocation {

    final String tagName;
    final AbstractJsonLocation parent;

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public AbstractJsonLocation getParent() {
        return parent;
    }

    public JsonLocation(String tagName, AbstractJsonLocation parent) {
        this.tagName = tagName;
        this.parent = parent;
    }

    @Override
    public String toString(){
        return parent.toString()+"['"+tagName+"']";
    }
}
