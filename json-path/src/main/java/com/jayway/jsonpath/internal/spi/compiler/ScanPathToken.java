package com.jayway.jsonpath.internal.spi.compiler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
class ScanPathToken extends PathToken {

    @Override
    void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {

        if (isLeaf()) {
            ctx.addResult(currentPath, model);
        }

        Predicate predicate = createScanPredicate(next(), ctx);
        Map<String, Object> predicateMatches = new LinkedHashMap<String, Object>();

        walk(currentPath, model, ctx, predicate, predicateMatches);

        //Filters has already been evaluated
        PathToken next = next();
        if (next instanceof FilterPathToken) {
            if (next.isLeaf()) {
                for (Map.Entry<String, Object> match : predicateMatches.entrySet()) {
                    ctx.addResult(match.getKey(), match.getValue());
                }
                return;
            } else {
                next = next.next();
            }
        }

        for (Map.Entry<String, Object> match : predicateMatches.entrySet()) {
            next.evaluate(match.getKey(), match.getValue(), ctx);
        }
    }

    public void walk(String currentPath, Object model, EvaluationContextImpl ctx, Predicate predicate, Map<String, Object> predicateMatches) {
        if (ctx.jsonProvider().isMap(model)) {
            walkObject(currentPath, model, ctx, predicate, predicateMatches);
        } else if (ctx.jsonProvider().isArray(model)) {
            walkArray(currentPath, model, ctx, predicate, predicateMatches);
        }
    }

    public void walkArray(String currentPath, Object model, EvaluationContextImpl ctx, Predicate predicate, Map<String, Object> predicateMatches) {

        if (predicate.matches(model)) {
            predicateMatches.put(currentPath, model);
        }

        Iterable<Object> models = ctx.jsonProvider().toIterable(model);
        int idx = 0;
        for (Object evalModel : models) {
            String evalPath = currentPath + "[" + idx + "]";

            if (predicate.clazz().equals(FilterPathToken.class)) {
                if (predicate.matches(evalModel)) {
                    predicateMatches.put(evalPath, evalModel);
                }
            }

            walk(evalPath, evalModel, ctx, predicate, predicateMatches);
            idx++;
        }
    }

    public void walkObject(String currentPath, Object model, EvaluationContextImpl ctx, Predicate predicate, Map<String, Object> predicateMatches) {

        if (predicate.matches(model)) {
            predicateMatches.put(currentPath, model);
        }
        Collection<String> properties = ctx.jsonProvider().getPropertyKeys(model);

        for (String property : properties) {
            String evalPath = currentPath + "['" + property + "']";
            Object propertyModel = ctx.jsonProvider().getMapValue(model, property);
            walk(evalPath, propertyModel, ctx, predicate, predicateMatches);
        }

    }

    private Predicate createScanPredicate(final PathToken target, final EvaluationContextImpl ctx) {
        if (target instanceof PropertyPathToken) {
            return new Predicate() {
                private PropertyPathToken propertyPathToken = (PropertyPathToken) target;

                @Override
                public Class<?> clazz() {
                    return PropertyPathToken.class;
                }

                @Override
                public boolean matches(Object model) {
                    Collection<String> keys = ctx.jsonProvider().getPropertyKeys(model);
                    return keys.containsAll(propertyPathToken.getProperties());
                }
            };
        } else if (target instanceof ArrayPathToken) {
            return new Predicate() {

                @Override
                public Class<?> clazz() {
                    return ArrayPathToken.class;
                }

                @Override
                public boolean matches(Object model) {
                    return ctx.jsonProvider().isArray(model);
                }
            };
        } else if (target instanceof WildcardPathToken) {
            return new Predicate() {

                @Override
                public Class<?> clazz() {
                    return WildcardPathToken.class;
                }

                @Override
                public boolean matches(Object model) {
                    return true;
                }
            };
        } else if (target instanceof FilterPathToken) {
            return new Predicate() {
                private FilterPathToken filterPathToken = (FilterPathToken) target;

                @Override
                public Class<?> clazz() {
                    return FilterPathToken.class;
                }

                @Override
                public boolean matches(Object model) {
                    return filterPathToken.accept(model, ctx.configuration());
                }
            };
        } else {
            return new Predicate() {
                @Override
                public Class<?> clazz() {
                    return null;
                }

                @Override
                public boolean matches(Object model) {
                    return false;
                }
            };
        }
    }


    @Override
    boolean isTokenDefinite() {
        return false;
    }

    @Override
    public String getPathFragment() {
        return "..";
    }

    private interface Predicate {
        Class<?> clazz();

        boolean matches(Object model);
    }
}
