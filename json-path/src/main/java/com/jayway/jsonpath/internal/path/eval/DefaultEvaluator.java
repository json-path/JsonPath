package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.token.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultEvaluator implements EvaluatorDispatcher {
  /**
   * Type safe cache abstraction
   */
  protected class EvaluatorCache {
    protected final LinkedHashMap<Class<? extends PathToken>, ITokenEvaluator> evaluators = new LinkedHashMap<Class<? extends PathToken>, ITokenEvaluator>();

    public <P extends PathToken, E extends ITokenEvaluator<P>> void put(final Class<P> tokenClass, final E evaluator) {
      evaluators.put(tokenClass, evaluator);
    }

    public <P extends PathToken, E extends ITokenEvaluator<P>> E get(final P token) {
      final Class<? extends PathToken> wantClass = token.getClass();
      for (final Map.Entry<Class<? extends PathToken>, ITokenEvaluator> pair : evaluators.entrySet()) {
        if (pair.getKey().isAssignableFrom(wantClass)) {
          //noinspection unchecked
          return (E) pair.getValue();
        }
      }
      throw new IllegalArgumentException("Unknown PathToken type: " + wantClass.getCanonicalName());
    }
  }

  protected final EvaluatorCache evaluators;

  public DefaultEvaluator() {
    evaluators = new EvaluatorCache();
    evaluators.put(IndexArrayPathToken.class, new IndexArrayTokenEvaluator(this));
    evaluators.put(SliceArrayPathToken.class, new SliceArrayTokenEvaluator(this));
    evaluators.put(FunctionPathToken.class,   new FunctionTokenEvaluator(this));
    evaluators.put(PredicatePathToken.class,  new PredicateTokenEvaluator(this));
    evaluators.put(PropertyPathToken.class,   new PropertyTokenEvaluator(this));
    evaluators.put(RootPathToken.class,       new RootTokenEvaluator(this));
    evaluators.put(ScanPathToken.class,       new ScanTokenEvaluator(this));
    evaluators.put(WildcardPathToken.class,   new WildcardTokenEvaluator(this));
  }

  @Override
  public void evaluate(final PathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    evaluators.get(token).evaluate(token, currentPath, parent, model, ctx);
  }
}
