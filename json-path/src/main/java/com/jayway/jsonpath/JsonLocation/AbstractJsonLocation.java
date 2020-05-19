package com.jayway.jsonpath.JsonLocation;

public abstract class AbstractJsonLocation {
    public abstract String getTagName();

    public abstract AbstractJsonLocation getParent();

    public abstract String toString();
}
