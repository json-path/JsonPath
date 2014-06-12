package com.jayway.jsonpath.internal.spi.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.compiler.EvaluationContext;
import com.jayway.jsonpath.spi.compiler.Path;
import com.jayway.jsonpath.spi.json.JsonProvider;

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
    private final Path path;
    private int resultIndex = 0;

    EvaluationContextImpl(Path path, Configuration configuration) {
        this.path = path;
        this.configuration = configuration;
        this.objectResult = configuration.getProvider().createArray();
        this.pathResult = new ArrayList<String>();

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
        if (path.isDefinite()) {
            return (T) jsonProvider().getArrayIndex(objectResult, 0);
        }
        return (T) objectResult;
    }

    @Override
    public Object getWithOptions() {
        boolean optAsPathList = configuration.getOptions().contains(Option.AS_PATH_LIST);
        boolean optAlwaysReturnList = configuration.getOptions().contains(Option.ALWAYS_RETURN_LIST);

        if (optAsPathList) {
            Object array = configuration.getProvider().createArray();
            int i = 0;
            for (String p : pathResult) {
                configuration.getProvider().setProperty(array, i, p);
                i++;
            }
            return array;
        } else if (optAlwaysReturnList) {
            return objectResult;
        } else {
            return get();
        }
    }

    @Override
    public List<String> getPathList() {
        return pathResult;
    }

}
