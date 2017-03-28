package com.jayway.jsonpath.internal.function.latebinding;

/**
 * Obtain the late binding value at runtime rather than storing the value in the cache thus trashing the cache
 *
 * Created by mattg on 3/27/17.
 */
public interface ILateBindingValue {
    Object get();
}
