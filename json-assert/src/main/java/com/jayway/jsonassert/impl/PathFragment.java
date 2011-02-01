package com.jayway.jsonassert.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: kalle stenflo
 * Date: 1/23/11
 * Time: 1:55 PM
 */
class PathFragment {


    public static final PathFragment WILDCARD_FRAGMENT = new PathFragment("*", false);

    //matches array index accessers like : [1], [23]
    private static final Pattern ARRAY_POSITION_PATTER = Pattern.compile("\\[(\\d*)\\]");

    //matches groovy style array index accessers like : get(1), get(23)
    private static final Pattern GROOVY_POSITION_PATTER = Pattern.compile("get\\((\\d*)\\)");

    //matches array wildcard : [*]
    private static final Pattern ARRAY_WILDCARD_PATTER = Pattern.compile("\\[\\*\\]");

    //matches groovy style array wildcard : [*]
    private static final Pattern GROOVY_WILDCARD_PATTER = Pattern.compile("get\\(\\*\\)");

    private final String value;

    private final boolean leaf;

    /**
     * Creates a new path fragment
     *
     * @param value value of path fragment
     * @param leaf
     */
    PathFragment(String value, boolean leaf) {
        this.value = value;
        this.leaf = leaf;
    }

    @Override
    public String toString() {
        return "JSONPathFragment{" +
                "value='" + value + '\'' +
                '}';
    }

    /**
     *
     * @return the value of this path fragment
     */
    String value() {
        return value;
    }

    /**
     * Utility method to rebuild a path from path fragments
     *
     * @param path the path to append this path fragment to
     *
     * @return the new extended path
     */
    String appendToPath(String path){
        StringBuilder builder = new StringBuilder(path);

        if(ARRAY_POSITION_PATTER.matcher(value).matches()){
            builder.append("[").append(getArrayIndex()).append("]");
        }
        else if(GROOVY_POSITION_PATTER.matcher(value).matches()){
            builder.append(path.isEmpty()?"":".").append("get(").append(getArrayIndex()).append(")");
        }
        else if(ARRAY_WILDCARD_PATTER.matcher(value).matches()){
            builder.append("[*]");
        }
        else if(GROOVY_WILDCARD_PATTER.matcher(value).matches()){
            builder.append(path.isEmpty()?"":".").append("get(*)");
        }
        else {
           builder.append(path.isEmpty()?"":".").append(value);
        }
        return builder.toString();
    }

    boolean isLeaf(){
        return leaf;
    }

    /**
     * Check if this path fragment is a array index
     *
     * @return true if this fragment is an array index
     */
    boolean isArrayIndex() {
        return ARRAY_POSITION_PATTER.matcher(value).matches() || GROOVY_POSITION_PATTER.matcher(value).matches();
    }

    /**
     * Check if this path fragment is an array wildcard
     *
     * @return true if this fragment is an array wildcard
     */
    boolean isArrayWildcard() {
        return ARRAY_WILDCARD_PATTER.matcher(value).matches() || GROOVY_WILDCARD_PATTER.matcher(value).matches();
    }

    boolean isWildcard(){
        return "*".endsWith(value());
    }

    /**
     * returns the int index of this path fragment. If this is not an array index fragment
     * an UnsupportedOperationException is thrown
     *
     * @return the array index of this fragment
     */
    int getArrayIndex() {
        Matcher matcher = ARRAY_POSITION_PATTER.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        matcher = GROOVY_POSITION_PATTER.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new UnsupportedOperationException("not an array index fragment");
    }
}
