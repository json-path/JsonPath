package com.jayway.jsonpath.internal.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.jayway.jsonpath.internal.Utils.notNull;

/**
 *
 */
public class EvaluationContextImpl implements EvaluationContext {

    private final Configuration configuration;
    private final Object valueResult;
    private final Object pathResult;
    private final Path path;
    private int resultIndex = 0;

    public EvaluationContextImpl(Path path, Configuration configuration) {
        notNull(path, "path can not be null");
        notNull(configuration, "configuration can not be null");
        this.path = path;
        this.configuration = configuration;
        this.valueResult = configuration.jsonProvider().createArray();
        this.pathResult = configuration.jsonProvider().createArray();
    }

    public void addResult(String path, Object model) {
        configuration.jsonProvider().setProperty(valueResult, resultIndex, model);
        configuration.jsonProvider().setProperty(pathResult, resultIndex, path);
        resultIndex++;
    }

    public JsonProvider jsonProvider() {
        return configuration.jsonProvider();
    }

    public Set<Option> options() {
        return configuration.getOptions();
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        if (path.isDefinite()) {
            if(resultIndex == 0){
                throw new PathNotFoundException("No results for path: " + path.toString());
            }
            return (T) jsonProvider().getArrayIndex(valueResult, 0);
        }
        return (T)valueResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPath() {
        if(resultIndex == 0){
            throw new PathNotFoundException("No results for path: " + path.toString());
        }
        return (T)pathResult;
    }

    @Override
    public List<String> getPathList() {
        List<String> res = new ArrayList<String>();
        if(resultIndex > 0){
            Iterable<?> objects = configuration.jsonProvider().toIterable(pathResult);
            for (Object o : objects) {
                res.add((String)o);
            }
        }
        return res;
    }

}
