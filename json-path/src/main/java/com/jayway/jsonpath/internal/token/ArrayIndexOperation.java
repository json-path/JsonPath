package com.jayway.jsonpath.internal.token;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Character.isDigit;

public class ArrayIndexOperation {

    private final List<Integer> indexes;

    private ArrayIndexOperation(List<Integer> indexes) {
        this.indexes = Collections.unmodifiableList(indexes);
    }

    public List<Integer> indexes() {
        return indexes;
    }

    public boolean isSingleIndexOperation(){
        return indexes.size() == 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(Utils.join(",", indexes));
        sb.append("]");

        return sb.toString();
    }

    public static ArrayIndexOperation parse(String operation) {
        //check valid chars
        for (int i = 0; i < operation.length(); i++) {
            char c = operation.charAt(i);
            if (!isDigit(c) && c != ',') {
                throw new InvalidPathException("Failed to parse ArrayIndexOperation: " + operation);
            }
        }
        String[] tokens = operation.split(",");

        List<Integer> tempIndexes = new ArrayList<Integer>();
        for (String token : tokens) {
            tempIndexes.add(parseInteger(token));
        }

        return new ArrayIndexOperation(tempIndexes);
    }

    private static Integer parseInteger(String token) {
        try {
            return Integer.parseInt(token);
        } catch (Exception e){
            throw new InvalidPathException("Failed to parse token in ArrayIndexOperation: " + token, e);
        }
    }
}
