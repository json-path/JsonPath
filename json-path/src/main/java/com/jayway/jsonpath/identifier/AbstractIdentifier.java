package com.jayway.jsonpath.identifier;

public abstract class AbstractIdentifier {
    public abstract String getTagName();

    public abstract AbstractIdentifier getParent();

    public abstract String toString();
}
