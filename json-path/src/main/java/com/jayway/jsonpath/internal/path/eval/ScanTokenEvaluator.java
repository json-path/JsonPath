package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.*;
import com.jayway.jsonpath.internal.path.predicate.*;
import com.jayway.jsonpath.internal.path.token.*;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.Collection;

public class ScanTokenEvaluator extends TokenEvaluator<ScanPathToken> {
  public ScanTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final ScanPathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    PathToken pt = token.next();

    walk(pt, currentPath, parent,  model, ctx, createScanPredicate(pt, ctx));

  }

  public void walk(PathToken pt, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx, PathTokenPredicate predicate) {
    if (ctx.jsonProvider().isMap(model)) {
      walkObject(pt, currentPath, parent, model, ctx, predicate);
    } else if (ctx.jsonProvider().isArray(model)) {
      walkArray(pt, currentPath, parent, model, ctx, predicate);
    }
  }

  public void walkArray(PathToken pt, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx, PathTokenPredicate predicate) {

    if (predicate.matches(model)) {
      if (pt.isLeaf()) {
        dispatcher.evaluate(pt, currentPath, parent, model, ctx);
      } else {
        PathToken next = pt.next();
        Iterable<?> models = ctx.jsonProvider().toIterable(model);
        int idx = 0;
        for (Object evalModel : models) {
          String evalPath = currentPath + "[" + idx + "]";
          dispatcher.evaluate(next, evalPath, parent, evalModel, ctx);
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

  public void walkObject(PathToken pt, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx, PathTokenPredicate predicate) {

    if (predicate.matches(model)) {
      dispatcher.evaluate(pt, currentPath, parent, model, ctx);
    }
    Collection<String> properties = ctx.jsonProvider().getPropertyKeys(model);

    for (String property : properties) {
      String evalPath = currentPath + "['" + property + "']";
      Object propertyModel = ctx.jsonProvider().getMapValue(model, property);
      if (propertyModel != JsonProvider.UNDEFINED) {
        walk(pt, evalPath, PathRef.create(model, property), propertyModel, ctx, predicate);
      }
    }
  }

  public static PathTokenPredicate createScanPredicate(final PathToken target, final EvaluationContextImpl ctx) {
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

  private static final PathTokenPredicate FALSE_PREDICATE = new PathTokenPredicate() {

    @Override
    public boolean matches(Object model) {
      return false;
    }
  };
}
