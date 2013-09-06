package com.jayway.jsonpath;

/**
 * User: kalle
 * Date: 8/30/13
 * Time: 12:03 PM
 */
public interface ReadContext {

    Object json();

    <T> T read(String path, Filter... filters);

    <T> T read(JsonPath path);
}
