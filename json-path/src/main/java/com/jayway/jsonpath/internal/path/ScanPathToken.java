/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.spi.json.JsonProvider;
import net.minidev.json.JSONArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class ScanPathToken extends PathToken {

    ScanPathToken() {
    }

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

        PathToken pt = next();

        walk(pt, currentPath, parent,  model, ctx, createScanPredicate(pt, ctx));
    }

    public static void walk(PathToken pt, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx, Predicate predicate) {
        if (ctx.jsonProvider().isMap(model)) {
            walkObject(pt, currentPath, parent, model, ctx, predicate);
        } else if (ctx.jsonProvider().isArray(model)) {
            walkArray(pt, currentPath, parent, model, ctx, predicate);
        }
    }

    public static void walkArray(PathToken pt, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx, Predicate predicate) {

        if (predicate.matches(model)) {
            if (pt.isLeaf()) {
                pt.evaluate(currentPath, parent, model, ctx);
            } else {
                PathToken next = pt.next();
                Iterable<?> models = ctx.jsonProvider().toIterable(model);
                int idx = 0;
                for (Object evalModel : models) {
                    String evalPath = currentPath + "[" + idx + "]";
                    next.setUpstreamArrayIndex(idx);
                    next.evaluate(evalPath, parent, evalModel, ctx);
                    idx++;
                }
            }
        }

        Iterable<?> models = ctx.jsonProvider().toIterable(model);
        int idx = 0;
        for (Object evalModel : models) {
            String evalPath = currentPath + "[" + idx + "]";
            walk(pt, evalPath, PathRef.create(model, idx), evalModel, ctx, predicate);
            idx++;
        }
    }

    public static void walkObject(PathToken pt, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx, Predicate predicate) {
        List<Integer> indexList=null;
        if((!pt.isLeaf()) && pt.next() instanceof ArrayIndexToken && pt.next().isLeaf()){
            ArrayIndexToken token=(ArrayIndexToken)pt.next();
            try {
                Field arrayIndexOperation=token.getClass().getDeclaredField("arrayIndexOperation");
                arrayIndexOperation.setAccessible(true);
                ArrayIndexOperation operation=(ArrayIndexOperation) arrayIndexOperation.get(token);
                Field indexField=operation.getClass().getDeclaredField("indexes");
                indexField.setAccessible(true);
                indexList=(List<Integer>) indexField.get(operation);
                pt.setNext(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
//            token.arr
        }
        if (predicate.matches(model)) {
            pt.evaluate(currentPath, parent, model, ctx);
        }
        Collection<String> properties = ctx.jsonProvider().getPropertyKeys(model);

        for (String property : properties) {
            String evalPath = currentPath + "['" + property + "']";
            Object propertyModel = ctx.jsonProvider().getMapValue(model, property);
            if (propertyModel != JsonProvider.UNDEFINED) {
                walk(pt, evalPath, PathRef.create(model, property), propertyModel, ctx, predicate);
            }
        }
        if(indexList!=null){
            Object result=ctx.getValueResult();
            if(result instanceof JSONArray){
                JSONArray actualValueRes=(JSONArray) result,actualPathResult=(JSONArray)ctx.getPathResult() ;
                List<Object> valueStore=new ArrayList<>();
                List<Object> pathStore=new ArrayList<>();
                for(int i=0;i<indexList.size();i++){
                    int index=indexList.get(i);
                    if(index<actualValueRes.size()){
                        valueStore.add(actualValueRes.get(index));
                        pathStore.add(actualPathResult.get(index));
                    }
                }
                actualValueRes.clear();
                actualPathResult.clear();
                actualValueRes.addAll(valueStore);
                actualPathResult.addAll(pathStore);
            }
        }
    }

    private static Predicate createScanPredicate(final PathToken target, final EvaluationContextImpl ctx) {
        if (target instanceof PropertyPathToken) {
            return new PropertyPathTokenPredicate(target, ctx);
        } else if (target instanceof ArrayPathToken) {
            return new ArrayPathTokenPredicate(ctx);
        } else if (target instanceof WildcardPathToken) {
            return new WildcardPathTokenPredicate();
        } else if (target instanceof PredicatePathToken) {
            return new FilterPathTokenPredicate(target, ctx);
        } else {
            return FALSE_PREDICATE;
        }
    }


    @Override
    public boolean isTokenDefinite() {
        return false;
    }

    @Override
    public String getPathFragment() {
        return "..";
    }

    private interface Predicate {
        boolean matches(Object model);
    }

    private static final Predicate FALSE_PREDICATE = new Predicate() {

        @Override
        public boolean matches(Object model) {
            return false;
        }
    };

    private static final class FilterPathTokenPredicate implements Predicate {
        private final EvaluationContextImpl ctx;
        private PredicatePathToken predicatePathToken;

        private FilterPathTokenPredicate(PathToken target, EvaluationContextImpl ctx) {
            this.ctx = ctx;
            predicatePathToken = (PredicatePathToken) target;
        }

        @Override
        public boolean matches(Object model) {
            return predicatePathToken.accept(model, ctx.rootDocument(), ctx.configuration(), ctx);
        }
    }

    private static final class WildcardPathTokenPredicate implements Predicate {

        @Override
        public boolean matches(Object model) {
            return true;
        }
    }

    private static final class ArrayPathTokenPredicate implements Predicate {
        private final EvaluationContextImpl ctx;

        private ArrayPathTokenPredicate(EvaluationContextImpl ctx) {
            this.ctx = ctx;
        }

        @Override
        public boolean matches(Object model) {
            return ctx.jsonProvider().isArray(model);
        }
    }

    private static final class PropertyPathTokenPredicate implements Predicate {
        private final EvaluationContextImpl ctx;
        private PropertyPathToken propertyPathToken;

        private PropertyPathTokenPredicate(PathToken target, EvaluationContextImpl ctx) {
            this.ctx = ctx;
            propertyPathToken = (PropertyPathToken) target;
        }

        @Override
        public boolean matches(Object model) {

            if (! ctx.jsonProvider().isMap(model)) {
                return false;
            }

//
// The commented code below makes it really hard understand, use and predict the result
// of deep scanning operations. It might be correct but was decided to be
// left out until the behavior of REQUIRE_PROPERTIES is more strictly defined
// in a deep scanning scenario. For details read conversation in commit
// https://github.com/jayway/JsonPath/commit/1a72fc078deb16995e323442bfb681bd715ce45a#commitcomment-14616092
//
//            if (ctx.options().contains(Option.REQUIRE_PROPERTIES)) {
//                // Have to require properties defined in path when an indefinite path is evaluated,
//                // so have to go there and search for it.
//                return true;
//            }

            if (! propertyPathToken.isTokenDefinite()) {
                // It's responsibility of PropertyPathToken code to handle indefinite scenario of properties,
                // so we'll allow it to do its job.
                return true;
            }

            if (propertyPathToken.isLeaf() && ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) {
                // In case of DEFAULT_PATH_LEAF_TO_NULL missing properties is not a problem.
                return true;
            }

            Collection<String> keys = ctx.jsonProvider().getPropertyKeys(model);
            return keys.containsAll(propertyPathToken.getProperties());
        }
    }
}
