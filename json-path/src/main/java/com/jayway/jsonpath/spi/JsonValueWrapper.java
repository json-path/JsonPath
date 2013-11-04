package com.jayway.jsonpath.spi;

/**
 * Interface to allow wrapping a JSON value to maintain metadata associated with the value
 *
 * @author Mike Buchanan
 */
public interface JsonValueWrapper<T> {
    public T getValue();
}
