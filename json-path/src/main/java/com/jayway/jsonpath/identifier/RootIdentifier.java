package com.jayway.jsonpath.identifier;

public class RootIdentifier extends Identifier{

    public RootIdentifier() {
        super("$",null);
    }

    public String toString(){
        return "$";
    }
}
