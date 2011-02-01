package com.jayway.jsonassert;

import java.util.List;
import java.util.Map;

/**
 * User: kallestenflo
 * Date: 1/24/11
 * Time: 9:29 PM
 */
public interface JsonPath {

    /**
     * Get a new reader with the given path as root
     *
     * @param path to extract a reader for
     * @return new reader
     */
    JsonPath getReader(String path);

    /**
     * @param path
     * @return
     */
    boolean hasJsonPath(String path);

    /**
     * @param path
     * @return
     */
    boolean isNull(String path);

    /**
     * @param path
     * @return                
     */
    <T> T get(String path);

    /**
     * @param path
     * @return
     */
    String getString(String path);

    /**
     * @param path
     * @return
     */
    Long getLong(String path);

    /**
     * @param path
     * @return
     */
    Double getDouble(String path);

    /**
     * @param path
     * @return
     */
    Boolean getBoolean(String path);

    /**
     * @param path
     * @param <T>
     * @return
     */
    <T> List<T> getList(String path);

    /**
     * @param path
     * @return
     */
    Map<String, Object> getMap(String path);
}
