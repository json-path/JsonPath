package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.token.PropertyPathToken;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonpath.internal.Utils.onlyOneIsTrueNonThrow;

public class PropertyTokenEvaluator extends TokenEvaluator<PropertyPathToken> {
  public PropertyTokenEvaluator(final EvaluatorDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public void evaluate(final PropertyPathToken token, final String currentPath, final PathRef parent, final Object model, final EvaluationContextImpl ctx) {
    // Can't assert it in ctor because isLeaf() could be changed later on.
    assert onlyOneIsTrueNonThrow(token.singlePropertyCase(), token.multiPropertyMergeCase(), token.multiPropertyIterationCase());

    if (!ctx.jsonProvider().isMap(model)) {
      if (! token.isUpstreamDefinite()) {
        return;
      } else {
        String m = model == null ? "null" : model.getClass().getName();

        throw new PathNotFoundException(String.format(
          "Expected to find an object with property %s in path %s but found '%s'. " +
            "This is not a json object according to the JsonProvider: '%s'.",
          token.getPathFragment(), currentPath, m, ctx.configuration().jsonProvider().getClass().getName()));
      }
    }

    if (token.singlePropertyCase() || token.multiPropertyMergeCase()) {
      handleObjectProperty(token, currentPath, model, ctx, token.getProperties());
      return;
    }

    assert token.multiPropertyIterationCase();
    final List<String> currentlyHandledProperty = new ArrayList<String>(1);
    currentlyHandledProperty.add(null);
    for (final String property : token.getProperties()) {
      currentlyHandledProperty.set(0, property);
      handleObjectProperty(token, currentPath, model, ctx, currentlyHandledProperty);
    }
  }
}
