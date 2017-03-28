package com.jayway.jsonpath.internal.function.latebinding;

/**
 * Obtain the late binding value at runtime rather than storing the value in the cache thus trashing the cache
 *
 * Created by mattg on 3/27/17.
 */
public interface ILateBindingValue {
    /**
     * Obtain the value of the parameter at runtime using the parameter state and invocation of other late binding values
     * rather than maintaining cached state which ends up in a global store and won't change as a result of external
     * reference changes.
     *
     * @return
     *      The value of evaluating the context at runtime.
     */
    Object get();
}
