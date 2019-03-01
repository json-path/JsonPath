package com.jayway.jsonpath.identifier;

public  class Identifier extends AbstractIdentifier {

    final String tagName;
    final AbstractIdentifier parent;

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public AbstractIdentifier getParent() {
        return parent;
    }

    public Identifier(String tagName, AbstractIdentifier parent) {
        this.tagName = tagName;
        this.parent = parent;
    }

    @Override
    public String toString(){
        return parent.toString()+"['"+tagName+"']";
    }
}
