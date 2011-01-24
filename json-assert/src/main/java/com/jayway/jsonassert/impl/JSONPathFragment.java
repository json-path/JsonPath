package com.jayway.jsonassert.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: kalle stenflo
 * Date: 1/23/11
 * Time: 1:55 PM
 */
class JSONPathFragment {

    private static final Pattern ARRAY_POSITION_PATTER = Pattern.compile("\\[(\\d*)\\]");
    private static final Pattern GROOVY_POSITION_PATTER = Pattern.compile("get\\((\\d*)\\)");

    private static final Pattern ARRAY_WILDCARD_PATTER = Pattern.compile("\\[\\*\\]");
    private static final Pattern GROOVY_WILDCARD_PATTER = Pattern.compile("get\\(\\*\\)");

    private final String fragment;

    JSONPathFragment(String fragment) {
        this.fragment = fragment;
    }

    String fragment() {
        return fragment;
    }

    boolean isArrayIndex() {
        return ARRAY_POSITION_PATTER.matcher(fragment).matches() || GROOVY_POSITION_PATTER.matcher(fragment).matches();
    }

    boolean isArrayWildcard() {
        return ARRAY_WILDCARD_PATTER.matcher(fragment).matches() || GROOVY_WILDCARD_PATTER.matcher(fragment).matches();
    }

    int getArrayIndex() {
        Matcher matcher = ARRAY_POSITION_PATTER.matcher(fragment);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        matcher = GROOVY_POSITION_PATTER.matcher(fragment);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("not an array index fragment");
    }
}
