package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.InvalidPathException;

import static java.lang.Character.isDigit;

public class ArraySliceOperation {

    public enum Operation {
        SLICE_FROM,
        SLICE_TO,
        SLICE_BETWEEN
    }

    private final Integer from;
    private final Integer to;
    private final Operation operation;

    private ArraySliceOperation(Integer from, Integer to, Operation operation) {
        this.from = from;
        this.to = to;
        this.operation = operation;
    }

    public Integer from() {
        return from;
    }

    public Integer to() {
        return to;
    }

    public Operation operation() {
        return operation;
    }

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
        Operation tempOperpation;

        if(tempFrom != null && tempTo == null){
            tempOperpation  = Operation.SLICE_FROM;
        } else if(tempFrom != null && tempTo != null){
            tempOperpation  = Operation.SLICE_BETWEEN;
        } else if(tempFrom == null && tempTo != null){
            tempOperpation  = Operation.SLICE_TO;
        } else {
            throw new InvalidPathException("Failed to parse SliceOperation: " + operation);
        }

        return new ArraySliceOperation(tempFrom, tempTo, tempOperpation);
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
