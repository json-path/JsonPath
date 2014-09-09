package com.jayway.jsonpath.internal.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Predicate;

import java.util.Collection;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 *
 */
public class FilterPathToken extends PathToken {

    private static final String[] FRAGMENTS = {
            "[?]",
            "[?,?]",
            "[?,?,?]",
            "[?,?,?,?]",
            "[?,?,?,?,?]"
    };

    private final Collection<Predicate> filters;

    public FilterPathToken(Predicate filter) {
        this.filters = asList(filter);
    }

    public FilterPathToken(Collection<Predicate> filters) {
        this.filters = filters;
    }

    @Override
    public void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (!ctx.jsonProvider().isArray(model)) {
            throw new InvalidPathException(format("Filter: %s can only be applied to arrays. Current context is: %s", toString(), model));
        }
        int idx = 0;
        Iterable<Object> objects = ctx.jsonProvider().toIterable(model);

        for (Object idxModel : objects) {
            if (accept(idxModel, ctx.configuration())) {
                handleArrayIndex(idx, currentPath, model, ctx);
            }
            idx++;
        }
    }

    public boolean accept(final Object obj, final Configuration configuration) {
        boolean accept = true;

        Predicate.PredicateContext ctx = new Predicate.PredicateContext() {
            @Override
            public Object target() {
                return obj;
            }

            @Override
            public Configuration configuration() {
                return configuration;
            }
        };

        for (Predicate filter : filters) {
            if (!filter.apply (ctx)) {
                accept = false;
                break;
            }
        }

        return accept;
    }

    @Override
    public String getPathFragment() {
        return FRAGMENTS[filters.size() - 1];
    }

    @Override
    boolean isTokenDefinite() {
        return false;
    }
}
