package com.jayway.jsonpath.internal.spi.compiler;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;

import java.util.Collection;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 *
 */
class FilterPathToken extends PathToken {

    private static final String[] FRAGMENTS = {
            "[?]",
            "[?,?]",
            "[?,?,?]",
            "[?,?,?,?]",
            "[?,?,?,?,?]"
    };

    private final Collection<Filter> filters;

    public FilterPathToken(Filter filter) {
        this.filters = asList(filter);
    }

    public FilterPathToken(Collection<Filter> filters) {
        this.filters = filters;
    }

    @Override
    void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
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

    public boolean accept(Object obj, Configuration configuration) {
        boolean accept = true;

        for (Filter filter : filters) {
            if (!filter.apply (obj, configuration)) {
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
