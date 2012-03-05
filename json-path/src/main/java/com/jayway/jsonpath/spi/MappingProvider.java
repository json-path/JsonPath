package com.jayway.jsonpath.spi;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/5/12
 * Time: 11:03 AM
 */
public interface MappingProvider {


    public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException;

    public <T extends Collection<E>, E> T convertValue(Object fromValue, Class<T> collectionType, Class<E> elementType) throws IllegalArgumentException;

}
