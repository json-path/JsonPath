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
public class PredicatePathToken extends PathToken {

    private static final String[] FRAGMENTS = {
            "[?]",
            "[?,?]",
            "[?,?,?]",
            "[?,?,?,?]",
            "[?,?,?,?,?]"
    };

    private final Collection<Predicate> filters;

    public PredicatePathToken(Predicate filter) {
        this.filters = asList(filter);
    }

    public PredicatePathToken(Collection<Predicate> filters) {
        this.filters = filters;
    }

    @Override
    public void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (ctx.jsonProvider().isMap(model)) {
            if (accept(model, ctx.configuration())) {
                if (isLeaf()) {
                    ctx.addResult(currentPath, model);
                } else {
                    next().evaluate(currentPath, model, ctx);
                }
            }
        } else if (ctx.jsonProvider().isArray(model)){
            int idx = 0;
            Iterable<Object> objects = ctx.jsonProvider().toIterable(model);

            for (Object idxModel : objects) {
                if (accept(idxModel, ctx.configuration())) {
                    handleArrayIndex(idx, currentPath, model, ctx);
                }
                idx++;
            }
        } else {
            throw new InvalidPathException(format("Filter: %s can not be applied to primitives. Current context is: %s", toString(), model));
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
