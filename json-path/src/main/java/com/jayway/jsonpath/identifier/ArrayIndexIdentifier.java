package com.jayway.jsonpath.identifier;

public class ArrayIndexIdentifier extends Identifier {
    final int count;

    public ArrayIndexIdentifier(AbstractIdentifier parent, int count) {
        super(parent.getTagName(),parent);
        this.count = count;
    }

    public String toString(){
        return parent.toString()+"["+count+"]";
    }
}
