package com.jayway.jsonpath.spi.compiler;

import com.jayway.jsonpath.Configuration;

import java.util.List;

/**
 *
 */
public interface EvaluationContext {
    Configuration configuration();

    <T> T get();

    List<String> getPathList();
}
