package com.jayway.jsonpath.internal.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Predicate;

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
    public Object contextDocument() {
        return contextDocument;
    }

    @Override
    public Object rootDocument() {
        return rootDocument;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

}