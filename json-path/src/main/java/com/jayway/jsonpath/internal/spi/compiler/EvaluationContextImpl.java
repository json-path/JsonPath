package com.jayway.jsonpath.internal.spi.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.compiler.EvaluationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
class EvaluationContextImpl implements EvaluationContext {

    private final Configuration configuration;
    private final Object objectResult;
    private final List<String> pathResult;
    private final boolean isDefinite;
    private int resultIndex = 0;

    EvaluationContextImpl(Configuration configuration, boolean isDefinite) {
        this.configuration = configuration;
        this.objectResult = configuration.getProvider().createArray();
        this.pathResult = new ArrayList<String>();
        this.isDefinite = isDefinite;
    }

    void addResult(String path, Object model) {
        pathResult.add(path);
        configuration.getProvider().setProperty(objectResult, resultIndex, model);
        resultIndex++;
    }

    public JsonProvider jsonProvider() {
        return configuration.getProvider();
    }

    public Set<Option> options() {
        return configuration.getOptions();
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }


    @Override
    public <T> T get() {
        if (isDefinite) {
            return (T) jsonProvider().getProperty(objectResult, 0);
        }
        return (T) objectResult;
    }

    @Override
    public List<String> getPathList() {
        return pathResult;
    }

}
