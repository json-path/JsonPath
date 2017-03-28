package com.jayway.jsonpath.internal.function.latebinding;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.function.ParamType;

/**
 * Defines the contract for late bindings, provides document state and enough context to perform the evaluation at a later
 * date such that we can operate on a dynamically changing value.
 *
 * Acts like a lambda function with references, but since we're supporting JDK 6+, we're left doing this...
 *
 * Created by mattg on 3/27/17.
 */
public class PathLateBindingValue implements ILateBindingValue {
    private final Path path;
    private final Object rootDocument;
    private final Configuration configuration;

    public PathLateBindingValue(ParamType type, Path path, Object rootDocument, Configuration configuration) {
        this.path = path;
        this.rootDocument = rootDocument;
        this.configuration = configuration;
    }

    /**
     * Evaluate the field type
     * @return
     */
    public Object get() {
        return path.evaluate(rootDocument, rootDocument, configuration).getValue();
    }
}
