package com.jayway.jsonpath.internal.spi.mapper;

public interface Factory<T> {

    T createInstance();
}
