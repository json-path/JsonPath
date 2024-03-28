package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;
import static com.jayway.jsonpath.internal.Utils.*;
import static com.jayway.jsonpath.Option.AS_PATH_LIST;

public class PathEvaluator {

    private final Path path;
    private final Configuration configuration;

    public PathEvaluator(Path path, Configuration configuration) {
        notNull(path, "path can not be null");
        notNull(configuration, "configuration can not be null");
        this.path = path;
        this.configuration = configuration;
    }

    public <T> T evaluate(Object jsonObject) {
        boolean optAsPathList = configuration.containsOption(AS_PATH_LIST);
        EvaluationContext evaluationContext = path.evaluate(jsonObject, jsonObject, configuration);


        return resultByConfiguration(jsonObject, evaluationContext);
    }

    private <T> T resultByConfiguration(Object jsonObject, EvaluationContext evaluationContext) {
        if(configuration.containsOption(AS_PATH_LIST)){
            return (T)evaluationContext.getPathList();
        } else {
            return (T) jsonObject;
        }
    }

}