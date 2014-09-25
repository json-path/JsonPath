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
package com.jayway.jsonpath.internal.token;

import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class ScanPathToken extends PathToken {

    @Override
    public void evaluate(String currentPath, Object model, EvaluationContextImpl ctx) {

        if (isLeaf()) {
            ctx.addResult(currentPath, model);
        }

        Predicate predicate = createScanPredicate(next(), ctx);
        Map<String, Object> predicateMatches = new LinkedHashMap<String, Object>();

        walk(currentPath, model, ctx, predicate, predicateMatches);

        //Filters has already been evaluated
        PathToken next = next();
        if (next instanceof PredicatePathToken) {
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

    public static void walk(String currentPath, Object model, EvaluationContextImpl ctx, Predicate predicate, Map<String, Object> predicateMatches) {
        if (ctx.jsonProvider().isMap(model)) {
            walkObject(currentPath, model, ctx, predicate, predicateMatches);
        } else if (ctx.jsonProvider().isArray(model)) {
            walkArray(currentPath, model, ctx, predicate, predicateMatches);
        }
    }

    public static void walkArray(String currentPath, Object model, EvaluationContextImpl ctx, Predicate predicate, Map<String, Object> predicateMatches) {

        if (predicate.matches(model)) {
            predicateMatches.put(currentPath, model);
        }

        Iterable<?> models = ctx.jsonProvider().toIterable(model);
        int idx = 0;
        for (Object evalModel : models) {
            String evalPath = currentPath + "[" + idx + "]";

            if (predicate.clazz().equals(PredicatePathToken.class)) {
                if (predicate.matches(evalModel)) {
                    predicateMatches.put(evalPath, evalModel);
                }
            }

            walk(evalPath, evalModel, ctx, predicate, predicateMatches);
            idx++;
        }
    }

    public static void walkObject(String currentPath, Object model, EvaluationContextImpl ctx, Predicate predicate, Map<String, Object> predicateMatches) {

        if (predicate.matches(model)) {
            predicateMatches.put(currentPath, model);
        }
        Collection<String> properties = ctx.jsonProvider().getPropertyKeys(model);

        for (String property : properties) {
            String evalPath = currentPath + "['" + property + "']";
            Object propertyModel = ctx.jsonProvider().getMapValue(model, property);
            if (propertyModel != JsonProvider.UNDEFINED) {
                walk(evalPath, propertyModel, ctx, predicate, predicateMatches);
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
    boolean isTokenDefinite() {
        return false;
    }

    @Override
    public String getPathFragment() {
        return "..";
    }

    private static interface Predicate {
        Class<?> clazz();

        boolean matches(Object model);
    }

    private static final Predicate FALSE_PREDICATE = new Predicate() {
      @Override
      public Class<?> clazz() {
          return null;
      }

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
      public Class<?> clazz() {
          return PredicatePathToken.class;
      }

      @Override
      public boolean matches(Object model) {
          return predicatePathToken.accept(model, ctx.rootDocument(), ctx.configuration());
      }
    }

    private static final class WildcardPathTokenPredicate implements Predicate {
      @Override
      public Class<?> clazz() {
          return WildcardPathToken.class;
      }

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
      public Class<?> clazz() {
          return ArrayPathToken.class;
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
      public Class<?> clazz() {
          return PropertyPathToken.class;
      }

      @Override
      public boolean matches(Object model) {
          if(ctx.jsonProvider().isMap(model)){
            Collection<String> keys = ctx.jsonProvider().getPropertyKeys(model);
            return keys.containsAll(propertyPathToken.getProperties());
          }
          return false;
      }
    }
}
