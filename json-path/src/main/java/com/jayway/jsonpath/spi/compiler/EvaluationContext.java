package com.jayway.jsonpath.spi.compiler;

import com.jayway.jsonpath.Configuration;

import java.util.List;

public interface EvaluationContext {

    /**
     *
     * @return the configuration used for this evaluation
     */
    Configuration configuration();

    /**
     * This method does not adhere to configuration settings. It will return a single object (not wrapped in a List) even if the
     * configuration contains the {@link com.jayway.jsonpath.Option#ALWAYS_RETURN_LIST}
     *
     * @param <T> expected return type
     * @return evaluation result
     */
    <T> T getValue();


    <T> T getPath();


    /**
     * Convenience method to get list of hits as String path representations
     *
     * @return list of path representations
     */
    List<String> getPathList();

}
