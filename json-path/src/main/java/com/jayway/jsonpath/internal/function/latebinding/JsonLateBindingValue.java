package com.jayway.jsonpath.internal.function.latebinding;

import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.spi.json.JsonProvider;

/**
 * Created by mattg on 3/27/17.
 */
public class JsonLateBindingValue implements ILateBindingValue {
    private final JsonProvider jsonProvider;
    private final Parameter jsonParameter;

    public JsonLateBindingValue(JsonProvider jsonProvider, Parameter jsonParameter) {
        this.jsonProvider = jsonProvider;
        this.jsonParameter = jsonParameter;
    }

    @Override
    public Object get() {
        return jsonProvider.parse(jsonParameter.getJson());
    }
}
