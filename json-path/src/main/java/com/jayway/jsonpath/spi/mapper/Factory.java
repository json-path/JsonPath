package com.jayway.jsonpath.spi.mapper;

public interface Factory<T> {

    T createInstance();
}
