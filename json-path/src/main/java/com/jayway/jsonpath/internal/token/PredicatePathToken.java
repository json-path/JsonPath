package com.jayway.jsonpath.internal.token;

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

    private final Collection<Predicate> predicates;

    public PredicatePathToken(Predicate filter) {
        this.predicates = asList(filter);
    }

    public PredicatePathToken(Collection<Predicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (ctx.jsonProvider().isMap(model)) {
            if (accept(model, ctx.rootDocument(), ctx.configuration())) {
                if (isLeaf()) {
                    ctx.addResult(currentPath, model);
                } else {
                    next().evaluate(currentPath, model, ctx);
                }
            }
        } else if (ctx.jsonProvider().isArray(model)){
            int idx = 0;
            Iterable<?> objects = ctx.jsonProvider().toIterable(model);

            for (Object idxModel : objects) {
                if (accept(idxModel, ctx.rootDocument(),  ctx.configuration())) {
                    handleArrayIndex(idx, currentPath, model, ctx);
                }
                idx++;
            }
        } else {
            throw new InvalidPathException(format("Filter: %s can not be applied to primitives. Current context is: %s", toString(), model));
        }
    }

    public boolean accept(final Object obj, final Object root, final Configuration configuration) {
        Predicate.PredicateContext ctx = new PredicateContextImpl(obj, root, configuration);

        for (Predicate predicate : predicates) {
            if (!predicate.apply (ctx)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getPathFragment() {
        return FRAGMENTS[predicates.size() - 1];
    }

    @Override
    boolean isTokenDefinite() {
        return false;
    }



}
