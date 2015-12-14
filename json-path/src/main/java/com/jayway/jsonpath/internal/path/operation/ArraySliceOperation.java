package com.jayway.jsonpath.internal.path.operation;

import com.jayway.jsonpath.InvalidPathException;

import static java.lang.Character.isDigit;

public abstract class ArraySliceOperation {

    public enum Operation {
        SLICE_FROM,
        SLICE_TO,
        SLICE_BETWEEN
    }

    private final Integer from;
    private final Integer to;

    protected ArraySliceOperation(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }

    public Integer from() {
        return from;
    }

    public Integer to() {
        return to;
    }

    public abstract Operation operation();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(from == null ? "" : from.toString());
        sb.append(":");
        sb.append(to == null ? "" : to.toString());
        sb.append("]");

        return sb.toString();
    }

    public static ArraySliceOperation parse(String operation){
        //check valid chars
        for (int i = 0; i < operation.length(); i++) {
            char c = operation.charAt(i);
            if( !isDigit(c)  && c != '-' && c != ':'){
                throw new InvalidPathException("Failed to parse SliceOperation: " + operation);
            }
        }
        String[] tokens = operation.split(":");

        Integer tempFrom = tryRead(tokens, 0);
        Integer tempTo = tryRead(tokens, 1);

        if(tempFrom != null && tempTo == null){
            return new SliceFromOperation(tempFrom);
        } else if(tempFrom != null){
            return new SliceBetweenOperation(tempFrom, tempTo);
        } else if(tempTo != null){
            return new SliceToOperation(tempTo);
        }
        throw new InvalidPathException("Failed to parse SliceOperation: " + operation);
    }

    private static Integer tryRead(String[] tokens, int idx){
        if(tokens.length > idx){
            if(tokens[idx].equals("")){
                return null;
            }
            return Integer.parseInt(tokens[idx]);
        } else {
            return null;
        }
    }
}
