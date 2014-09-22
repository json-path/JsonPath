package com.jayway.jsonpath.internal.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.spi.mapper.MappingException;

public class PredicateContextImpl implements Predicate.PredicateContext {
    private final Object contextDocument;
    private final Object rootDocument;
    private final Configuration configuration;

    public PredicateContextImpl(Object contextDocument, Object rootDocument, Configuration configuration) {
        this.contextDocument = contextDocument;
        this.rootDocument = rootDocument;
        this.configuration = configuration;
    }

    @Override
    public Object item() {
        return contextDocument;
    }

    @Override
    public <T> T item(Class<T> clazz) throws MappingException {
        return  configuration().mappingProvider().map(contextDocument, clazz, configuration);
    }

    @Override
    public Object root() {
        return rootDocument;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

}