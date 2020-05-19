package com.jayway.jsonpath.JsonLocation;

public class RootJsonLocation extends JsonLocation {

    public RootJsonLocation() {
        super("$",null);
    }

    public String toString(){
        return "$";
    }
}
