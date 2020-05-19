package com.jayway.jsonpath.JsonLocation;

public class ArrayIndexJsonLocation extends JsonLocation {
    final int count;

    public ArrayIndexJsonLocation(AbstractJsonLocation parent, int count) {
        super(count+"",parent);
        this.count = count;
    }

    public String toString(){
        return parent.toString()+"["+count+"]";
    }
}
